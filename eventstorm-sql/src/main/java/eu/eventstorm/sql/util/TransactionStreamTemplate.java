package eu.eventstorm.sql.util;

import java.util.stream.Stream;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.TransactionException;
import eu.eventstorm.sql.page.Page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionStreamTemplate {

    private final TransactionManager transactionManager;
    
    public TransactionStreamTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public <T> Stream<T> stream(TransactionCallback<Stream<T>> callback) {
    	
    	if (transactionManager.hasCurrent()) {
    		return executeInExistingTx(callback);
    	}
    	
    	Transaction tx = transactionManager.newTransactionReadOnly();
    	try {
    		return callback.doInTransaction().onClose(new OnCloseRunnable(tx));	
    	} catch (Exception cause) {
    		rollbackAndClose(tx);
    		throw cause;	
    	}
    	
    }

    public <T> Page<T> page(TransactionCallback<Page<T>> callback) {
    	
    	if (transactionManager.hasCurrent()) {
    		return executeInExistingTx(callback);
    	}
    	
    	Transaction tx = transactionManager.newTransactionReadOnly();
    	Page<T> page;
    	try {
    		page = callback.doInTransaction();	
    	} catch (Exception cause) {
    		rollbackAndClose(tx);
    		throw cause;	
    	}
    	page.getContent().onClose(new OnCloseRunnable(tx));
    	return page;
    }
    
    private <T> T executeInExistingTx(TransactionCallback<T> callback) {
    	if (!this.transactionManager.current().isReadOnly()) {
			throw new TransactionException(TransactionException.Type.READ_ONLY, transactionManager.current());
		}
		return callback.doInTransaction();
    }
    
    private static void rollbackAndClose(Transaction tx) {
    	try  {
			tx.rollback();
		} finally {
			tx.close();
		}
    }
    
    private static final class OnCloseRunnable implements Runnable {

    	private final Transaction tx;
    	
		private OnCloseRunnable(Transaction tx) {
			this.tx = tx;
		}

		@Override
		public void run() {
			try {
    			tx.rollback();	
    		} finally {
				tx.close();
			}
		}
    }
    
}
