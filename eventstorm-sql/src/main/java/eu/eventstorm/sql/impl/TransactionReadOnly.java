package eu.eventstorm.sql.impl;

import java.sql.Connection;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.TransactionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class TransactionReadOnly extends AbstractTransaction {

	TransactionReadOnly(TransactionManagerImpl transactionManager, Connection connection, TransactionDefinition definition) {
		super(transactionManager, connection, definition);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public TransactionQueryContext write(SqlQuery query) {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}

	@Override
	public TransactionSupport innerTransaction(TransactionDefinition definition) {
		if (TransactionType.READ_ONLY != definition.getType()) {
			throw new TransactionException(TransactionException.Type.READ_ONLY);
		}
		return new TransactionNested(this, getTransactionManager());
	}

	@Override
	public TransactionQueryContext writeAutoIncrement(SqlQuery query) {
		throw new TransactionException(TransactionException.Type.READ_ONLY);
	}
	
}
