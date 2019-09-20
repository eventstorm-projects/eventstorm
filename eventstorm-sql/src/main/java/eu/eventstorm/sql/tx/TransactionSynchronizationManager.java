package eu.eventstorm.sql.tx;

import java.util.IdentityHashMap;
import java.util.Map;

import eu.eventstorm.sql.Database;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionSynchronizationManager {

//	private static final Map<Integer, ThreadLocal<AbstractTransaction>> TRANSACTIONS = new IdentityHashMap<>(8);
	
//	private static final ThreadLocal<Transaction> TRANSACTIONS = new ThreadLocal<>();
//	
//	private TransactionSynchronizationManager() {
//	}
//
//	public static Transaction currentTransaction() {
//		return TRANSACTIONS.get();
//	}
//
//	public static void setTransaction(Transaction tx) {
//		TRANSACTIONS.set(tx);
//	}
//
//	public static void clear() {
//		TRANSACTIONS.remove();
//	}

	static void register(TransactionManagerImpl transactionManager) {
	//	TRANSACTIONS.put(transactionManager.getId(), new ThreadLocal<>());
	}

	public static TransactionContext current(Database database) {
	//	return TRANSACTIONS.get(database.transactionManager().getId()).get();
		return (TransactionContext)database.transactionManager().current();
	}

	static void clear(TransactionManager transactionManager) {
		//TRANSACTIONS.get(transactionManager.getId()).remove();
	}

	
}
