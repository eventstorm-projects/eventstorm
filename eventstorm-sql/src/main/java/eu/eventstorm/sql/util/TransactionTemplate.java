package eu.eventstorm.sql.util;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTemplate {

    protected final TransactionManager transactionManager;
    
    public TransactionTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public <T> T executeWithReadWrite(TransactionCallback<T> callback) {
    	  T returnValue;
          try (Transaction tx = transactionManager.newTransactionReadWrite()) {
              try {
                  returnValue = callback.doInTransaction();                    
                  tx.commit();
              } catch (Exception cause) {
                  tx.rollback();
                  throw cause;
              }
          } 
          return returnValue;
    }
    
    public <T> T executeWithReadOnly(TransactionCallback<T> callback) {
    	 T returnValue;
         try (Transaction tx = transactionManager.newTransactionReadOnly()) {
             try {
                 returnValue = callback.doInTransaction();                    
             } catch (Exception cause) {
                 throw cause;
             } finally {
           	  tx.rollback();
             }
         } 
         return returnValue;
    }
    
}
