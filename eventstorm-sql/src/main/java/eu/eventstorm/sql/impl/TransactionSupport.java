package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionDefinition;

interface TransactionSupport extends TransactionContext, Transaction {

	TransactionSupport innerTransaction(TransactionDefinition definition);
	
	boolean isMain();
	
}
