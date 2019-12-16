package eu.eventstorm.sql.impl;

import java.sql.Connection;

import eu.eventstorm.sql.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionReadOnly extends AbstractTransaction {

	TransactionReadOnly(TransactionManagerImpl transactionManager, Connection connection) {
		super(transactionManager, connection);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	protected void doCommit() {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}

	@Override
	public TransactionQueryContext write(String sql) {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}

	public Transaction innerTransaction(TransactionDefinition definition) {
		if (!definition.isReadOnly()) {
			throw new TransactionException(TransactionException.Type.READ_ONLY);
		}
		return new TransactionNested(this);
	}

	@Override
	public TransactionQueryContext writeAutoIncrement(String sql) {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}

}
