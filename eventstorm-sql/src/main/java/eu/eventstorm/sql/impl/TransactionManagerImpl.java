package eu.eventstorm.sql.impl;

import static eu.eventstorm.sql.impl.TransactionException.Type.CONNECTION_ISOLATION;
import static eu.eventstorm.sql.impl.TransactionException.Type.CREATE;
import static eu.eventstorm.sql.impl.TransactionException.Type.NO_CURRENT_TRANSACTION;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionManagerImpl implements TransactionManager {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagerImpl.class);

    private final DataSource dataSource;

    private final int defaultIsolationLevel;

    private final TransactionHolder transactions;

    private boolean enforceReadOnly = false;

    private final TransactionManagerConfiguration configuration;

    public TransactionManagerImpl(DataSource dataSource) {
        this(dataSource, TransactionManagerConfiguration.DEFAULT);
    }

    public TransactionManagerImpl(DataSource dataSource, TransactionManagerConfiguration configuration) {
    	this.dataSource = dataSource;
    	this.configuration = configuration;
        try (Connection conn = dataSource.getConnection()) {
            this.defaultIsolationLevel = conn.getTransactionIsolation();
        } catch (SQLException cause) {
            throw new TransactionException(CONNECTION_ISOLATION, cause);
        }
        LOGGER.info("Transaction defaultIsolationLevel : [{}]", defaultIsolationLevel);
        this.transactions = new TransactionHolder();
    }

    public void setEnforceReadOnly(boolean enforceReadOnly) {
        this.enforceReadOnly = enforceReadOnly;
    }

    @Override
    public Transaction newTransaction(TransactionDefinition definition) {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getTransaction({})", definition);
        }
        TransactionSupport tx = this.transactions.get();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("current Transaction ({})", tx);
        }

        if (tx != null) {
            if (TransactionType.ISOLATED_READ_WRITE == definition.getType()) {
                tx = new TransactionIsolatedReadWrite(this, getConnection(), tx, definition);
            }
            else {
                // get TX inside another TX
                tx = tx.innerTransaction(definition);
            }
        } else if (TransactionType.READ_ONLY == definition.getType()) {
            tx = new TransactionReadOnly(this, getConnection(), definition);
        } else if (TransactionType.ISOLATED_READ_WRITE == definition.getType()) {
            tx = new TransactionIsolatedReadWrite(this, getConnection(), null, definition);
        } else if (TransactionType.READ_WRITE == definition.getType()) {
            tx = new TransactionReadWrite(this, getConnection(), definition);
        }

        this.transactions.set(tx);
        return tx;
    }

    @Override
    public Transaction newTransactionReadOnly() {
        return newTransaction(TransactionDefinitions.READ_ONLY);
    }

    @Override
    public Transaction newTransactionReadWrite() {
        return newTransaction(TransactionDefinitions.READ_WRITE);
    }
    
    @Override
	public Transaction newTransactionIsolatedReadWrite() {
        return newTransaction(TransactionDefinitions.ISOLATED_READ_WRITE);
	}
    
	@Override
    public Transaction current() {
    	Transaction transaction = this.transactions.get();
    	if (transaction == null) {
            throw new TransactionException(NO_CURRENT_TRANSACTION);
        }
        return transaction;
    }

    @Override
	public boolean hasCurrent() {
		return this.transactions.get() != null;
	}
    
	Connection getConnection() {
    	final Connection conn;
        try {
            conn = dataSource.getConnection();
            //prepareTransactionalConnection(conn, definition);
        } catch (SQLException cause) {
            throw new TransactionException(CREATE, cause);
        }
        return conn;
    }

    void remove() {
		this.transactions.remove();
    }
    
    void restart(TransactionSupport transaction) {
    	
    	if (transaction instanceof AbstractTransaction) {
    		AbstractTransaction tx =(AbstractTransaction) transaction;
    		try {
				if (tx.getConnection().isClosed()) {
					this.transactions.remove();
				} else {
					this.transactions.set(transaction);
				}
			} catch (SQLException e) {
				this.transactions.remove();
			}
    	} else {
    		this.transactions.set(transaction);    		
    	}
        
    }

	@Override
	public TransactionContext context() {
		TransactionContext context = this.transactions.get();
		if (context == null) {
			throw new TransactionException(NO_CURRENT_TRANSACTION);
		}
		return context;
	}

	/*protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition)
            throws SQLException {
        if (enforceReadOnly && TransactionType.READ_ONLY == definition.getType()) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("SET TRANSACTION READ ONLY");
            }
        }
    }*/

	public TransactionManagerConfiguration getConfiguration() {
		return this.configuration;
	}
	
	protected final DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	public void close() {
		this.transactions.close();
	}

}
