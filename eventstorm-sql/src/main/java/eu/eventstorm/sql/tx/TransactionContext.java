package eu.eventstorm.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionContext {

	TransactionQueryContext read(String sql);

	TransactionQueryContext write(String sql);
	
	TransactionQueryContext writeAutoIncrement(String sql);

	void addHook(Runnable runnable);

}