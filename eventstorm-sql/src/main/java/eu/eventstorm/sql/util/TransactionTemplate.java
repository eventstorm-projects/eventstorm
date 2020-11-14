package eu.eventstorm.sql.util;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.TransactionException;
import eu.eventstorm.sql.page.Page;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTemplate.class);

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
			} finally {
				tx.rollback();
			}
		}
		return returnValue;
	}
	
	public void executeWithReadOnly(TransactionCallbackVoid callback) {
		try (Transaction tx = transactionManager.newTransactionReadOnly()) {
			try {
				callback.doInTransaction();
			} finally {
				tx.rollback();
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
	
	public void executeWithReadOnlySql(String sql) {
		try (Transaction tx = transactionManager.newTransactionReadOnly()) {
			try {
			} finally {
				tx.rollback();
			}
		}
	}

	public <T> Flux<T> flux(TransactionCallback<Stream<T>> callback) {

		Transaction tx = transactionManager.newTransactionReadOnly();
		
		OnCloseRunnable runnable = new OnCloseRunnable(tx);
		try {
			return Flux.fromStream(callback.doInTransaction())
					.doFinally(signal -> {
						try {
							runnable.run();
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
			throw new TransactionException(TransactionException.Type.READ_ONLY, transactionManager.current());
		}
		return callback.doInTransaction();
	}

	static void rollbackAndClose(Transaction tx) {
		try {
			tx.rollback();
		} finally {
			tx.close();
		}
	}

	static final class EncapsulatedTx<T> {

		private final OnCloseRunnable closeRunnable;
		private final TransactionCallback<Stream<T>> callback;

		EncapsulatedTx(OnCloseRunnable closeRunnable, TransactionCallback<Stream<T>> callback) {
			this.closeRunnable = closeRunnable;
			this.callback = callback;
		}

		public Stream<T> doInTransaction() {
			return callback.doInTransaction().onClose(closeRunnable);
		}

		@Override
		protected void finalize() throws Throwable {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("finalize");
			}
			
			try {
				closeRunnable.run();
			} finally {
				super.finalize();
			}
		}
	}
}
