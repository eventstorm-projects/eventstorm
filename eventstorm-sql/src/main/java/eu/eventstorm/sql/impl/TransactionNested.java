package eu.eventstorm.sql.impl;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.SqlQuery;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionNested implements TransactionSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionNested.class);

	private final AbstractTransaction main;
    private final TransactionSupport parent;
    private final TransactionManagerImpl transactionManager;
    private final int count;

    TransactionNested(AbstractTransaction main,  TransactionManagerImpl transactionManager) {
        this(main, main, transactionManager, 1);
    }
    
    TransactionNested(AbstractTransaction main, TransactionSupport parent, TransactionManagerImpl transactionManager, int count) {
    	this.main = main;
    	this.parent = parent;
        this.transactionManager = transactionManager;
        this.count = count;
    }

    @Override
    public boolean isReadOnly() {
        return parent.isReadOnly();
    }

    @Override
    public void commit() {
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("commit() in nested : skip");
    	}
        // skip
    }

    @Override
    public void rollback() {
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("rollback() in nested : skip");
    	}
        // skip
    }

    @Override
    public void close() {
    	this.transactionManager.restart(this.parent);
    }

    @Override
    public TransactionQueryContext read(SqlQuery query) {
        return this.parent.read(query);
    }

    @Override
    public TransactionQueryContext write(SqlQuery query) {
        return this.parent.write(query);
    }
    
    @Override
	public TransactionQueryContext writeAutoIncrement(SqlQuery query) {
    	 return this.parent.writeAutoIncrement(query);
	}

	@Override
	public UUID getUuid() {
		return parent.getUuid();
	}

	@Override
	public Instant getStart() {
		return parent.getStart();
	}

	@Override
	public TransactionSupport innerTransaction(TransactionDefinition definition) {
		return new TransactionNested(this.main, this, this.transactionManager, this.count + 1);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TransactionNested)) {
			return false;
		}
		
		TransactionNested otherNested = (TransactionNested) obj;
		return this.main.equals(otherNested.main) && this.count == otherNested.count;
	}

	@Override
	public boolean isMain() {
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.main.hashCode() + this.count;
	}
	
}