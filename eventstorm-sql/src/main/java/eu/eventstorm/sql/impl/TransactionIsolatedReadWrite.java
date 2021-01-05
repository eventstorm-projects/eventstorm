package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.TransactionDefinition;

import java.sql.Connection;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionIsolatedReadWrite extends TransactionReadWrite {

	private final TransactionSupport parent;

	TransactionIsolatedReadWrite(TransactionManagerImpl transactionManager, Connection connection, TransactionSupport parent, TransactionDefinition definition) {
		super(transactionManager, connection, definition);
		this.parent = parent;
	}

	@Override
	protected void afterCommit() {
		if (parent != null) {
			getTransactionManager().restart(parent);	
		}
	}

	@Override
	protected void afterRollback() {
		if (parent != null) {
			getTransactionManager().restart(parent);	
		}
	}

}
