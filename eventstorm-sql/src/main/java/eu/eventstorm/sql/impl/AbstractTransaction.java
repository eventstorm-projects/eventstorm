package eu.eventstorm.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.eventstorm.sql.TransactionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.tracer.TransactionSpan;
import eu.eventstorm.sql.tracer.TransactionTracer;
import eu.eventstorm.util.ToStringBuilder;

import static eu.eventstorm.sql.impl.TransactionException.Type.*;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractTransaction implements TransactionSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransaction.class);

	private final TransactionManagerImpl transactionManager;

	private final Connection connection;

	private final TransactionDefinition definition;

	private final UUID uuid;

	private boolean active;

	private final Map<String, PreparedStatement> statements = new HashMap<>();

	private final TransactionTracer tracer;

	private final TransactionSpan span;

	private final Instant instant;

	private final boolean mustRestoreAutoCommit;

	protected AbstractTransaction(TransactionManagerImpl transactionManager, Connection connection, TransactionDefinition definition) {
		this.transactionManager = transactionManager;
		this.definition = definition;
		this.connection = connection;
		this.mustRestoreAutoCommit = initAutoCommit(connection);
		this.uuid = UUID.randomUUID();
		this.active = true;
		this.tracer = transactionManager.getConfiguration().getTracer();
		this.span = this.tracer.begin(this);
		this.span.tag("uuid", this.uuid.toString());
		this.instant = Instant.now();
	}

	@Override
	public UUID getUuid() {
		return this.uuid;
	}

	@Override
	public Instant getStart() {
		return this.instant;
	}

	@Override
	public TransactionDefinition getDefinition() {
		return this.definition;
	}

	@Override
	public final void close() {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("close() active=[{}]", active);
		}
		TransactionException exception = null;
		try {

			if (active) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("call close() on a active transaction -> rollback");
				}
				try {
					rollback();
				} catch (TransactionException ex) {
					span.exception(ex);
					exception = ex;
				}
				active = false;
			}

			try {
				close(this.statements);
			} finally {
				try {
					close(connection);
				} catch (SQLException cause) {
					span.exception(cause);
					LOGGER.warn("Failed to close the connection", cause);
				}	
			}
			

		} finally {
			this.span.close();
		}	
		

		if (exception != null) {
			throw exception;
		}

	}

	@Override
	public TransactionQueryContext read(SqlQuery sql) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("select({})", sql);
		}
		return preparedStatement(sql, this.statements, Statement.NO_GENERATED_KEYS);
	}
	
	@Override
	public boolean isMain() {
		return true;
	}

	@Override
	public final void rollback() {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("rollback()");
		}
        
        try (TransactionSpan ignored = this.tracer.span("rollback")) {
            if (!this.active) {
                throw new TransactionException(NOT_ACTIVE, this);
            }

            try {
                this.connection.rollback();
				this.active = false;
            } catch (SQLException cause) {
                throw new TransactionException(ROLLBACK, this, null, cause);
            } finally {
				afterCommitOrRollback();
            }
        } finally {
            afterRollback();
        }
		
	}

	@Override
	public final void commit() {
		if (!this.active) {
			throw new TransactionException(NOT_ACTIVE, this);
		}

        try (TransactionSpan ignored = this.tracer.span("commit")) {
            try {
                this.connection.commit();
				this.active = false;
				afterCommitOrRollback();
            } catch (SQLException cause) {
                throw new TransactionException(COMMIT, this, null, cause);
            }
        } finally {
            afterCommit();
        }
	}

	private void afterCommitOrRollback() {
		try {
			transactionManager.remove();
		} finally {
			close(this.statements);
		}
	}

	protected void afterCommit() {
    }
    
    protected void afterRollback() {		
	}

	protected final Connection getConnection() {
		return connection;
	}

	protected final TransactionManagerImpl getTransactionManager() {
		return transactionManager;
	}

	protected final Map<String, PreparedStatement> getStatements() {
		return statements;
	}

	protected final void close(Map<String, PreparedStatement> map) {
		try {
			map.forEach((sql, ps) -> {
				try {
					ps.close();
				} catch (SQLException cause) {
					LOGGER.warn("Failed to close PreparedStatement -> skip", cause);
				}
			});
		} finally {
			map.clear();
		}
	}

	protected final TransactionQueryContext preparedStatement(SqlQuery query, Map<String, PreparedStatement> cache, int autoGeneratedKeys) {
		PreparedStatement ps = cache.get(query.sql());
		if (ps == null) {
			try {
				ps = this.tracer.decorate(this.connection.prepareStatement(query.sql(), autoGeneratedKeys));
			} catch (SQLException cause) {
				throw new TransactionException(PREPARED_STATEMENT, this, null, cause);
			}
			cache.put(query.sql(), ps);
		}
		return new TransactionQueryContextImpl(ps, query, this.transactionManager.getConfiguration());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractTransaction)) {
			return false;
		}
		return this.uuid.equals(((TransactionSupport)obj).getUuid());
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, false)
				.append("uuid", uuid)
				.append("instant", instant)
				.append("active", active)
				.toString();
	}

	private void close(Connection connection) throws SQLException {
		boolean isClosed = this.connection.isClosed();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("close() connection : isClosed=[{}] connection=[{}]", isClosed, connection);
		}
		if (!isClosed) {
			this.connection.setAutoCommit(mustRestoreAutoCommit);
			this.connection.close();
		}
	}

	private static boolean initAutoCommit(Connection conn) {
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
				return true;
			} else {
				return false;
			}
		} catch (SQLException cause) {
			throw new TransactionException(CREATE, cause);
		}
	}
}