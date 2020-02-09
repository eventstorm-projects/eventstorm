package eu.eventstorm.sql.util;

import java.util.function.Supplier;
import java.util.stream.Stream;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.TransactionException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionStreamTemplate {

    private final TransactionManager transactionManager;
    
    public TransactionStreamTemplate(Database database) {
        this.transactionManager = database.transactionManager();
    }
    
    public <T> Stream<T> decorate(Supplier<Stream<T>> supplier) {
    	if (transactionManager.hasCurrent()) {
    		return decorateOnCurrentTransaction(supplier);
    	}
    	
    	Transaction tx = transactionManager.newTransactionReadOnly();
    	return supplier.get().onClose(() -> {
    		try {
    			tx.rollback();	
    		} finally {
				tx.close();
			}
    	});
    	
    }

	private <T> Stream<T> decorateOnCurrentTransaction(Supplier<Stream<T>> supplier) {
		if (!this.transactionManager.current().isReadOnly()) {
			throw new TransactionException(TransactionException.Type.READ_ONLY, transactionManager.current());
		}
		
		return supplier.get();
		
	}
    
}
