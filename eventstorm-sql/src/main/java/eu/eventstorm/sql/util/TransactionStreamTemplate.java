package eu.eventstorm.sql.util;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.TransactionException;
import eu.eventstorm.sql.page.Page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionStreamTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionStreamTemplate.class);
	
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

	static final class OnCloseRunnable implements Runnable {

		private final Transaction tx;
		private boolean enable = true;

		OnCloseRunnable(Transaction tx) {
			this.tx = tx;
		}

		@Override
		public void run() {
			if (!enable) {
				return;
			}
			enable = false;
			try {
				tx.rollback();
			} finally {
				tx.close();
			}
		}
	}

}
