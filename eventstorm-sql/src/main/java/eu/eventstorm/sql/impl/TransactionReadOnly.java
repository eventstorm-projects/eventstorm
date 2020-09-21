package eu.eventstorm.sql.impl;

import java.sql.Connection;

import eu.eventstorm.sql.SqlQuery;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class TransactionReadOnly extends AbstractTransaction {

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
	public TransactionQueryContext write(SqlQuery query) {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}

	@Override
	public TransactionSupport innerTransaction(TransactionDefinition definition) {
		if (!definition.isReadOnly()) {
			throw new TransactionException(TransactionException.Type.READ_ONLY);
		}
		return new TransactionNested(this, getTransactionManager());
	}

	@Override
	public TransactionQueryContext writeAutoIncrement(SqlQuery query) {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}
	
}
