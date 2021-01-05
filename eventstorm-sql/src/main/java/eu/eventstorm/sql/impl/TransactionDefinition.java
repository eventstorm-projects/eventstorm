package eu.eventstorm.sql.impl;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
interface TransactionDefinition {

	/**
	 * @return Return whether to optimize as a read-only transaction.
	 */
	default boolean isReadOnly() {
		return false;
	}
}
