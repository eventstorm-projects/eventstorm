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
    
    public TransactionStreamTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public <T> Stream<T> decorate(TransactionCallback<Stream<T>> callback) {
    	
    	if (transactionManager.hasCurrent()) {
    		if (!this.transactionManager.current().isReadOnly()) {
    			throw new TransactionException(TransactionException.Type.READ_ONLY, transactionManager.current());
    		}
    		return callback.doInTransaction();
    	}
    	
    	Transaction tx = transactionManager.newTransactionReadOnly();
    	return callback.doInTransaction().onClose(() -> {
    		try {
    			tx.rollback();	
    		} finally {
				tx.close();
			}
    	});
    }
    
}
