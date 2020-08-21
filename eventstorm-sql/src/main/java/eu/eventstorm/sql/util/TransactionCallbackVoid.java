package eu.eventstorm.sql.util;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface TransactionCallbackVoid {

	void doInTransaction();
	
}
