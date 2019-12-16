package eu.eventstorm.sql.util;

import java.util.function.Supplier;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTemplate {

    private final Database database;
    
    public TransactionTemplate(Database database) {
        this.database = database;
    }

    public <T> TransactionTemplateBuilder<T> withReadWriteTransaction() {
        return new TransactionTemplateBuilderReadWrite<T>(database);
    }
    
    public <T> TransactionTemplateBuilder<T> withReadOnlyTransaction() {
        return new TransactionTemplateBuilderReadOnly<T>(database);
    }

    public static abstract class TransactionTemplateBuilder<T> {
        
        protected final Database database;
        
        protected Supplier<T> supplier;

        public TransactionTemplateBuilder(Database database) {
            this.database = database;
        }

        public TransactionTemplateBuilder<T> supply(Supplier<T> supplier) {
            this.supplier = supplier;
            return this;
        }
        
        public abstract T execute();
        
    }
    
    static final class TransactionTemplateBuilderReadWrite<T> extends TransactionTemplateBuilder<T> {

        public TransactionTemplateBuilderReadWrite(Database database) {
            super(database);
        }
        
        public T execute() {
            T returnValue;
            try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
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

        public TransactionTemplateBuilderReadOnly(Database database) {
            super(database);
        }
        
        public T execute() {
            T returnValue;
            try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
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
