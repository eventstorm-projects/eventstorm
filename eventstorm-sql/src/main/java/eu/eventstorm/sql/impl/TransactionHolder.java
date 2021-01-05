package eu.eventstorm.sql.impl;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.eventstorm.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionHolder implements AutoCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHolder.class);

	private final ConcurrentHashMap<Thread, TransactionSupport> holder;

	private final ScheduledExecutorService ses;

	TransactionHolder() {
		this.holder = new ConcurrentHashMap<>();
		ses = Executors.newScheduledThreadPool(1, new NamedThreadFactory("tx-cleaner-"));
		ses.scheduleAtFixedRate(new CleanerCommand(), 0, 1, TimeUnit.SECONDS);
	}

	TransactionSupport get() {
		return this.holder.get(Thread.currentThread());
	}

	void set(TransactionSupport tx) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("set({},{})", Thread.currentThread().getId(), tx);
		}
		this.holder.put(Thread.currentThread(), tx);
	}

	void remove() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("remove({})", Thread.currentThread());
		}
		this.holder.remove(Thread.currentThread());
	}

	private final class CleanerCommand implements Runnable {

		@Override
		public void run() {

			Instant now = Instant.now();

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Start Transaction Holder Cleaner on [{}] transaction(s)", holder.size());
			}
			holder.forEach((th, tx) -> {

				if (tx.getStart().plus(tx.getDefinition().getTimeout(), ChronoUnit.SECONDS).isBefore(now)) {
					LOGGER.warn("transaction timeout({}) for [{}] : [{}]-[{}] ",tx.getDefinition().getTimeout(), th, now, tx);
					th.interrupt();
					holder.remove(th);
					return;
				}

				if (tx.isMain()) {
					AbstractTransaction a = (AbstractTransaction) tx;
					try {
						if (a.getConnection().isClosed()) {
							// closed connection => remove
							LOGGER.info("Remove TX [{}] for Thread [{}] : connection is closed", tx, th);
							holder.remove(th);
						}
					} catch (SQLException cause) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Failed to check if connection is closed [{}] -> remove", cause.getMessage());
						}
						holder.remove(th);
					}
				} else {
					// check inner transaction ...
				}
			});
		}
	}

	@Override
	public void close() {
		LOGGER.info("close()");
		try {
			try {
				this.holder.forEach((th, tx) -> {
					try {
						try {
							tx.rollback();
						} finally {
							tx.close();
						}
					} catch (Exception cause) {
						LOGGER.warn("Failed to close tx [{}] for thread [{}]", tx, th);
					}
				});
			} finally {
				this.holder.clear();
			}
		} finally {
			ses.shutdown();
		}
	}

}
