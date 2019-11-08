package eu.eventstorm.sql.impl;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
enum TransactionDefinition {

	READ_ONLY(true), READ_WRITE(false), ISOLATED_READ_WRITE(false);
	
	private final boolean isReadOnly;
	
    private TransactionDefinition(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
     * @return Return whether to optimize as a read-only transaction.
     */
    boolean isReadOnly() {
    	return this.isReadOnly;
    }

}
