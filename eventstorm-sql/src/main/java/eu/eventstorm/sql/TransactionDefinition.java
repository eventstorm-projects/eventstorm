package eu.eventstorm.sql;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionDefinition {

    /**
     * @return the timeout of the transaction in seconds
     */
    int getTimeout();

    TransactionType getType();

}
