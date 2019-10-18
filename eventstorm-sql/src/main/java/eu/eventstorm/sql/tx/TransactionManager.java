package eu.eventstorm.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionManager {

    Transaction newTransactionReadOnly();

    Transaction newTransactionReadWrite();

    Transaction current();
    
    TransactionContext context();
    
}