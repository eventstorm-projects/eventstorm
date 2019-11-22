package eu.eventstorm.sql.impl;

import java.sql.Connection;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionIsolatedReadWrite extends TransactionReadWrite {

    private final AbstractTransaction parent;

    TransactionIsolatedReadWrite(TransactionManagerImpl transactionManager, Connection connection, AbstractTransaction parent) {
        super(transactionManager, connection);
        this.parent = parent;
    }

    protected void afterCommit() {
		getTransactionManager().restart(parent);
    }
    
     protected void afterRollback() {
		getTransactionManager().restart(parent);
	}

}
