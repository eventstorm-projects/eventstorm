package eu.eventstorm.sql.util;

import java.util.function.Supplier;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTemplate {

    private final TransactionManager transactionManager;
    
    public TransactionTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public <T> TransactionTemplateBuilder<T> withReadWriteTransaction() {
        return new TransactionTemplateBuilderReadWrite<>(transactionManager);
    }
    
    public <T> TransactionTemplateBuilder<T> withReadOnlyTransaction() {
        return new TransactionTemplateBuilderReadOnly<>(transactionManager);
    }

    public abstract static class TransactionTemplateBuilder<T> {
        
        protected final TransactionManager transactionManager;
        
        protected Supplier<T> supplier;

        public TransactionTemplateBuilder(TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }

        public TransactionTemplateBuilder<T> supply(Supplier<T> supplier) {
            this.supplier = supplier;
            return this;
        }
        
        public abstract T execute();
        
    }
    
    static final class TransactionTemplateBuilderReadWrite<T> extends TransactionTemplateBuilder<T> {

        public TransactionTemplateBuilderReadWrite(TransactionManager transactionManager) {
            super(transactionManager);
        }
        
        public T execute() {
            T returnValue;
            try (Transaction tx = transactionManager.newTransactionReadWrite()) {
                try {
                    returnValue = supplier.get();                    
                    tx.commit();
                } catch (Exception cause) {
                    tx.rollback();
                    throw cause;
                }
            } 
            return returnValue;
        }
        
    }

    
    static final class TransactionTemplateBuilderReadOnly<T> extends TransactionTemplateBuilder<T> {

        public TransactionTemplateBuilderReadOnly(TransactionManager transactionManager) {
            super(transactionManager);
        }
        
        public T execute() {
            T returnValue;
            try (Transaction tx = transactionManager.newTransactionReadOnly()) {
                try {
                    returnValue = supplier.get();                    
                    tx.rollback();
                } catch (Exception cause) {
                    tx.rollback();
                    throw cause;
                }
            } 
            return returnValue;
        }
        
    }

}
