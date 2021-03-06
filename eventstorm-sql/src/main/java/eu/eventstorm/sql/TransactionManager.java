package eu.eventstorm.sql;

import eu.eventstorm.sql.impl.TransactionContext;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionManager extends AutoCloseable {

    Transaction newTransaction(TransactionDefinition definition);

    Transaction newTransactionReadOnly();

    Transaction newTransactionReadWrite();
    
    Transaction newTransactionIsolatedReadWrite();
    
    Transaction current();

    boolean hasCurrent();

    TransactionContext context();

	@Override
	void close();

}