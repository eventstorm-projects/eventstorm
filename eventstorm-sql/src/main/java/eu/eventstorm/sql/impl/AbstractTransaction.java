package eu.eventstorm.sql.impl;

import static eu.eventstorm.sql.impl.TransactionException.Type.COMMIT;
import static eu.eventstorm.sql.impl.TransactionException.Type.NOT_ACTIVE;
import static eu.eventstorm.sql.impl.TransactionException.Type.PREPARED_STATEMENT;
import static eu.eventstorm.sql.impl.TransactionException.Type.ROLLBACK;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.tracer.TransactionSpan;
import eu.eventstorm.sql.tracer.TransactionTracer;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractTransaction implements TransactionSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransaction.class);

	private final TransactionManagerImpl transactionManager;

	private final Connection connection;

	private final UUID uuid;

	private boolean active;

	private final Map<String, PreparedStatement> select = new HashMap<>();

	private final TransactionTracer tracer;

	private final TransactionSpan span;

	protected AbstractTransaction(TransactionManagerImpl transactionManager, Connection connection) {
		this.transactionManager = transactionManager;
		this.connection = connection;
		this.uuid = UUID.randomUUID();
		this.active = true;
		this.tracer = transactionManager.getConfiguration().getTracer();
		this.span = this.tracer.begin(this);
		this.span.tag("uuid", this.uuid.toString());
	}

	@Override
	public UUID getUuid() {
		return this.uuid;
	}

	@Override
	public final void close() {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("close() active=[{}]", active);
		}
		TransactionException exception = null;
		try {

			if (active) {
				LOGGER.info("call close() on a active transaction -> rollback");
				try {
					rollback();
				} catch (TransactionException ex) {
					span.exception(ex);
					exception = ex;
				}
				active = false;
			}

			try {
				close(this.select);	
			} finally {
				try {
					boolean isClosed = this.connection.isClosed();
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("close() connection : isClosed=[{}] connection=[{}]", isClosed, connection);
					}
					if (!isClosed) {
						this.connection.close();
					}
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
		return preparedStatement(sql, this.select, Statement.NO_GENERATED_KEYS);
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
        
        try (TransactionSpan span = this.tracer.span("rollback")) {
            if (!this.active) {
                throw new TransactionException(NOT_ACTIVE, this);
            }

            try {
                this.connection.rollback();
            } catch (SQLException cause) {
                throw new TransactionException(ROLLBACK, this, null, cause);
            } finally {
                this.active = false;
                transactionManager.remove();
            }
        } finally {
            afterRollback();
        }
		
	}

	@Override
	public final void commit() {
        
        try (TransactionSpan span = this.tracer.span("commit")) {
          if (!this.active) {
                throw new TransactionException(NOT_ACTIVE, this);
            }
            try {
                doCommit();
            } catch (SQLException cause) {
                throw new TransactionException(COMMIT, this, null, cause);
            } finally {
                this.active = false;
                transactionManager.remove();
            }
        } finally {
            afterCommit();
        }
	}

	protected abstract void doCommit() throws SQLException;

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

	protected final void deactivate() {
		this.active = false;
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
	protected void finalize() throws Throwable {
		try {
			try {
				if (this.active) {
					this.connection.rollback();
				}	
			} finally {
				if (!this.connection.isClosed()) {
					this.connection.close();	
				}
			}
		} finally {
			super.finalize();	
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, false)
				.append("uuid", uuid)
				.append("active", active)
				.toString();
	}
	
}