package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.Transaction;

interface TransactionSupport extends TransactionContext, Transaction {

	TransactionSupport innerTransaction(TransactionDefinition definition);
	
	boolean isMain();
	
}
