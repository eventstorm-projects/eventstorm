package eu.eventstorm.sql.impl;

import static eu.eventstorm.sql.impl.TransactionException.Type.CONNECTION_ISOLATION;
import static eu.eventstorm.sql.impl.TransactionException.Type.CREATE;
import static eu.eventstorm.sql.impl.TransactionException.Type.NO_CURRENT_TRANSACTION;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

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

    private final ThreadLocal<TransactionSupport> transactions;

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
        this.transactions = new ThreadLocal<>();
    }

    public void setEnforceReadOnly(boolean enforceReadOnly) {
        this.enforceReadOnly = enforceReadOnly;
    }

    @Override
    public Transaction newTransactionReadOnly() {
        return this.getTransaction(TransactionDefinition.READ_ONLY);
    }

    @Override
    public Transaction newTransactionReadWrite() {
        return getTransaction(TransactionDefinition.READ_WRITE);
    }
    
    @Override
	public Transaction newTransactionIsolatedReadWrite() {
    	return getTransaction(TransactionDefinition.ISOLATED_READ_WRITE);
	}
    
	@Override
	public Transaction newTransactionIsolatedRead() {
		return getTransaction(TransactionDefinition.ISOLATED_READ);
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

    public Transaction getTransaction(TransactionDefinition definition) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getTransaction({})", definition);
        }
        TransactionSupport tx = this.transactions.get();
        
		if (tx != null) {

			if (TransactionDefinition.ISOLATED_READ_WRITE == definition) {
				tx = new TransactionIsolatedReadWrite(this, doBegin(definition), tx);
			} 
			else if (TransactionDefinition.ISOLATED_READ == definition) {
				tx = new TransactionIsolatedRead(this, doBegin(definition), tx);
			} 
			else {
				// get TX inside another TX
				tx = tx.innerTransaction(definition);
			}
		} else {
			switch (definition) {
			case READ_ONLY:
				tx = new TransactionReadOnly(this, doBegin(definition));
				break;
			case ISOLATED_READ_WRITE:
				tx = new TransactionIsolatedReadWrite(this, doBegin(definition), null);
				break;
			case READ_WRITE:
				tx = new TransactionReadWrite(this, doBegin(definition));
				break;
			case ISOLATED_READ:
				tx = new TransactionIsolatedRead(this, doBegin(definition), null);
				break;
			}
			
		}
        
        this.transactions.set(tx);
        return tx;
    }
    
	Connection doBegin(TransactionDefinition definition) {
    	final Connection conn;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            prepareTransactionalConnection(conn, definition);
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

	protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition)
            throws SQLException {
        if (enforceReadOnly && definition.isReadOnly()) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("SET TRANSACTION READ ONLY");
            }
        }
    }

	public TransactionManagerConfiguration getConfiguration() {
		return this.configuration;
	}
	
	protected final DataSource getDataSource() {
		return this.dataSource;
	}

}
