package eu.eventstorm.sql.util;

import eu.eventstorm.page.Page;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.lang.ref.Cleaner;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTemplate.class);

    private final TransactionManager transactionManager;

    public TransactionTemplate(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public <T> T executeWith(TransactionDefinition definition, TransactionCallback<T> callback) {
        T returnValue;
        try (Transaction tx = transactionManager.newTransaction(definition)) {
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

    public void executeWith(TransactionDefinition definition, TransactionCallbackVoid callback) {
        try (Transaction tx = transactionManager.newTransaction(definition)) {
            try {
                callback.doInTransaction();
                tx.commit();
            } catch (Exception cause) {
                tx.rollback();
                throw cause;
            }
        }
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

    public void executeWithReadWrite(TransactionCallbackVoid callback) {
        try (Transaction tx = transactionManager.newTransactionReadWrite()) {
            try {
                callback.doInTransaction();
                tx.commit();
            } catch (Exception cause) {
                tx.rollback();
                throw cause;
            }
        }
    }

    public <T> T executeWithReadOnly(TransactionCallback<T> callback) {
        T returnValue;
        try (Transaction tx = transactionManager.newTransactionReadOnly()) {
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

    public void executeWithReadOnly(TransactionCallbackVoid callback) {
        try (Transaction tx = transactionManager.newTransactionReadOnly()) {
            try {
                callback.doInTransaction();
                tx.commit();
            } catch (Exception cause) {
                tx.rollback();
                throw cause;
            }
        }
    }

    public <T> T executeWithIsolatedReadWrite(TransactionCallback<T> callback) {
        T returnValue;
        try (Transaction tx = transactionManager.newTransactionIsolatedReadWrite()) {
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

    public void executeWithIsolatedReadWrite(TransactionCallbackVoid callback) {
        try (Transaction tx = transactionManager.newTransactionIsolatedReadWrite()) {
            try {
                callback.doInTransaction();
                tx.commit();
            } catch (Exception cause) {
                tx.rollback();
                throw cause;
            }
        }
    }

    public <T> Stream<T> stream(TransactionCallback<Stream<T>> callback) {

        if (transactionManager.hasCurrent()) {
            return executeInExistingTx(callback);
        }
        Transaction tx = transactionManager.newTransactionReadOnly();
        try {
            return new EncapsulatedTx<>(new OnCloseRunnable(tx), callback).doInTransaction();
        } catch (Exception cause) {
            rollbackAndClose(tx);
            throw cause;
        }
    }

    public <T> void stream(TransactionCallback<Stream<T>> callback, Consumer<T> consumer) {

        if (transactionManager.hasCurrent()) {
            try (Stream<T> stream = executeInExistingTx(callback)) {
                stream.forEach(consumer);
            }
        }

        try (Transaction tx = transactionManager.newTransactionReadOnly()) {
            try (Stream<T> stream = callback.doInTransaction()) {
                stream.forEach(consumer);
                tx.commit();
            } catch (Exception cause) {
                rollbackAndClose(tx);
                throw cause;
            }
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
            page.getContent().onClose(new OnCloseRunnable(tx));
        } catch (Exception cause) {
            rollbackAndClose(tx);
            throw cause;
        }
        return page;
    }

    public <T> Flux<T> flux(TransactionCallback<Stream<T>> callback) {

        Transaction tx = transactionManager.newTransactionReadOnly();
        try {
            return Flux.fromStream(callback.doInTransaction())
                    .doFinally(signal -> {
                        try {
                            new OnCloseRunnable(tx).run();
                        } catch (Exception cause) {
                            LOGGER.debug("Failed to rollbackAndClose [{}]", cause.getMessage());
                        }
                    });
        } catch (Exception cause) {
            rollbackAndClose(tx);
            throw cause;
        }
    }

    private <T> T executeInExistingTx(TransactionCallback<T> callback) {
        if (!this.transactionManager.current().isReadOnly()) {
            throw new TransactionException(TransactionException.Type.READ_ONLY);
        }
        return callback.doInTransaction();
    }

    static void rollbackAndClose(Transaction tx) {
        try (tx) {
            tx.rollback();
        }
    }

    static final class EncapsulatedTx<T> {

        private static final Cleaner CLEANER = Cleaner.create();

        private final OnCloseRunnable closeRunnable;
        private final TransactionCallback<Stream<T>> callback;

        EncapsulatedTx(OnCloseRunnable closeRunnable, TransactionCallback<Stream<T>> callback) {
            this.closeRunnable = closeRunnable;
            this.callback = callback;
            CLEANER.register(this, closeRunnable);
        }

        public Stream<T> doInTransaction() {
            return callback.doInTransaction().onClose(closeRunnable);
        }

    }
}
