package eu.eventstorm.sql.impl;

import java.util.UUID;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionNested implements TransactionSupport {

    private final TransactionSupport parent;
    private final TransactionManagerImpl transactionManager;

    TransactionNested(TransactionSupport parent, TransactionManagerImpl transactionManager) {
        this.parent = parent;
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean isReadOnly() {
        return parent.isReadOnly();
    }

    @Override
    public void commit() {
        // skip
    }

    @Override
    public void rollback() {
        // skip
    }

    @Override
    public void close() {
    	this.transactionManager.restart(this.parent);
    }

    @Override
    public TransactionQueryContext read(String sql) {
        return this.parent.read(sql);
    }

    @Override
    public TransactionQueryContext write(String sql) {
        return this.parent.write(sql);
    }
    
    @Override
	public TransactionQueryContext writeAutoIncrement(String sql) {
    	 return this.parent.writeAutoIncrement(sql);
	}

    @Override
    public void addHook(Runnable runnable) {
        parent.addHook(runnable);
    }

	@Override
	public UUID getUuid() {
		return parent.getUuid();
	}
	
	@Override
	public TransactionSupport innerTransaction(TransactionDefinition definition) {
		return new TransactionNested(this, this.transactionManager);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TransactionSupport)) {
			return false;
		}
		return this.parent.equals(obj);
	}

	@Override
	public boolean isMain() {
		return false;
	}
	
	
	
}