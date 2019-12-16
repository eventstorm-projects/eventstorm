package eu.eventstorm.sql.impl;

import static eu.eventstorm.sql.impl.TransactionException.Type.COMMIT;
import static eu.eventstorm.sql.impl.TransactionException.Type.NOT_ACTIVE;
import static eu.eventstorm.sql.impl.TransactionException.Type.PREPARED_STATEMENT;
import static eu.eventstorm.sql.impl.TransactionException.Type.ROLLBACK;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.tracer.TransactionSpan;
import eu.eventstorm.sql.tracer.TransactionTracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractTransaction implements Transaction, TransactionContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransaction.class);

	private final TransactionManagerImpl transactionManager;

	private final Connection connection;

	private final UUID uuid;

	private final OffsetDateTime start;

	private boolean active;

	private final Map<String, PreparedStatement> select = new HashMap<>();

	private final List<Runnable> hooks;

	private final TransactionTracer tracer;

	private final TransactionSpan span;

	protected AbstractTransaction(TransactionManagerImpl transactionManager, Connection connection) {
		this.transactionManager = transactionManager;
		this.connection = connection;
		this.uuid = UUID.randomUUID();
		this.start = OffsetDateTime.now();
		this.active = true;
		this.hooks = new ArrayList<>();
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
			}

			try {
				if (!this.connection.isClosed()) {
					this.connection.close();
				} else {
					LOGGER.warn("connection is already closed()");
				}
			} catch (SQLException cause) {
				span.exception(cause);
				LOGGER.warn("Failed to close the connection", cause);
			} finally {
				close(this.select);
			}

		} finally {
			this.span.close();
		}

		if (exception != null) {
			throw exception;
		}

	}

	@Override
	public TransactionQueryContext read(String sql) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("select({})", sql);
		}
		return preparedStatement(sql, this.select, Statement.NO_GENERATED_KEYS);
	}

	@Override
	public final void rollback() {
        
        try {
            if (!this.active) {
                throw new TransactionException(NOT_ACTIVE, this, null);
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
        
        try {
          if (!this.active) {
                throw new TransactionException(NOT_ACTIVE, this, null);
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

	protected abstract Transaction innerTransaction(TransactionDefinition definition);

	@Override
	public final void addHook(Runnable runnable) {
		this.hooks.add(runnable);
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

	protected final TransactionQueryContext preparedStatement(String sql, Map<String, PreparedStatement> cache, int autoGeneratedKeys) {
		PreparedStatement ps = cache.get(sql);
		if (ps == null) {
			try {
				ps = this.tracer.decorate(this.connection.prepareStatement(sql, autoGeneratedKeys));
			} catch (SQLException cause) {
				throw new TransactionException(PREPARED_STATEMENT, this, null, cause);
			}
			cache.put(sql, ps);
		}
		return new TransactionQueryContextImpl(ps, sql, this.transactionManager.getConfiguration());
	}

	protected final void deactivate() {
		this.active = false;
	}

}