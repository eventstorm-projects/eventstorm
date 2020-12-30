package eu.eventstorm.sql.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class OnCloseRunnable implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(OnCloseRunnable.class);

	private final Transaction tx;
	private boolean enable = true;

	OnCloseRunnable(Transaction tx) {
		this.tx = tx;
	}

	@Override
	public void run() {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OnCloseRunnable.run() enable=[{}] tx=[{}]", enable, tx);
		}
		
		if (!enable) {
			return;
		}
		enable = false;
		try {
			tx.commit();
		} finally {
			tx.close();
		}
	}

}
