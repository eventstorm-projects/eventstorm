package eu.eventstorm.sql.impl;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionHolder implements AutoCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHolder.class);

	private final ConcurrentHashMap<Long, TransactionSupport> holder;

	private final ScheduledExecutorService ses;

	TransactionHolder() {
		this.holder = new ConcurrentHashMap<>();
		ses = Executors.newScheduledThreadPool(1, new DefaultThreadFactory());
		ses.scheduleAtFixedRate(new CleanerCommand(), 0, 1, TimeUnit.SECONDS);
	}

	TransactionSupport get() {
		return this.holder.get(Long.valueOf(Thread.currentThread().getId()));
	}

	void set(TransactionSupport tx) {
		this.holder.put(Long.valueOf(Thread.currentThread().getId()), tx);
	}

	void remove() {
		this.holder.remove(Long.valueOf(Thread.currentThread().getId()));
	}

	private final class CleanerCommand implements Runnable {

		@Override
		public void run() {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Start Transaction Holder Cleaner on [{}]", holder.size());
			}
			holder.forEach((th, tx) -> {
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

	/**
	 * The default thread factory
	 */
	static class DefaultThreadFactory implements ThreadFactory {
		private static final AtomicInteger POOL = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		DefaultThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			namePrefix = "tx-cleaner-" + POOL.getAndIncrement() + "-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
}
