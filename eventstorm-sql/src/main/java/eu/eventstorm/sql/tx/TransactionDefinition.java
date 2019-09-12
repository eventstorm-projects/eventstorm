package eu.eventstorm.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
interface TransactionDefinition {

    /**
     * @return Return whether to optimize as a read-only transaction.
     */
    boolean isReadOnly();

}
