package eu.eventstorm.sql.impl;

import java.sql.Connection;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionIsolatedRead extends TransactionReadOnly {

	private final TransactionSupport parent;

	TransactionIsolatedRead(TransactionManagerImpl transactionManager, Connection connection, TransactionSupport parent) {
		super(transactionManager, connection);
		this.parent = parent;
	}

	@Override
	protected void afterCommit() {
		getTransactionManager().restart(parent);
	}

	@Override
	protected void afterRollback() {
		getTransactionManager().restart(parent);
	}

}
