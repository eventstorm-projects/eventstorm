package eu.eventstorm.sql.tx;

import eu.eventstorm.sql.Database;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionSynchronizationManager {

	public static TransactionContext current(Database database) {
	//	return TRANSACTIONS.get(database.transactionManager().getId()).get();
		return (TransactionContext)database.transactionManager().current();
	}
	
}
