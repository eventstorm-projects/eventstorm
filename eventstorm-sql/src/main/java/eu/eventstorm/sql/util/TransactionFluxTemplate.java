package eu.eventstorm.sql.util;

import static eu.eventstorm.sql.util.TransactionStreamTemplate.rollbackAndClose;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.util.TransactionStreamTemplate.EncapsulatedTx;
import eu.eventstorm.sql.util.TransactionStreamTemplate.OnCloseRunnable;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionFluxTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFluxTemplate.class);
	
	private final TransactionManager transactionManager;
	

	public TransactionFluxTemplate(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public <T> Flux<T> flux(TransactionCallback<Stream<T>> callback) {

		Transaction tx = transactionManager.newTransactionIsolatedRead();
		try {
			return Flux.fromStream(new EncapsulatedTx<T>(new OnCloseRunnable(tx), callback).doInTransaction())
					.doOnError(ex -> {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("DoOnError : [{}]", ex.getMessage());
						}
						try {
							rollbackAndClose(tx);	
						} catch (Exception cause) {
							LOGGER.debug("Failed to rollbackAndClose [{}]", cause.getMessage());
						}
					});
		} catch (Exception cause) {
			rollbackAndClose(tx);
			throw cause;
		}
	}


}
