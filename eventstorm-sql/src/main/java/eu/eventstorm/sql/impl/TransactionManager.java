package eu.eventstorm.sql.impl;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionManager {

    Transaction newTransactionReadOnly();

    Transaction newTransactionReadWrite();
    
    Transaction newTransactionIsolatedReadWrite();

    Transaction current();

    boolean hasCurrent();

    TransactionContext context();

}