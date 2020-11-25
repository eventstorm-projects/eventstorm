package eu.eventstorm.batch;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;

import eu.eventstorm.batch.config.BatchProperties;

public final class BatchExecutor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchExecutor.class);

	private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

	public BatchExecutor(BatchProperties batchProperties) {
		this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		this.threadPoolTaskScheduler.setThreadNamePrefix(batchProperties.getPrefix());
		this.threadPoolTaskScheduler.setPoolSize(batchProperties.getPoolSize());
		this.threadPoolTaskScheduler.initialize();
	}

	public ListenableFuture<?> submit(BatchJob batchJob, BatchJobContext context) {

		ListenableFuture<?> future = this.threadPoolTaskScheduler.submitListenable(() -> batchJob.execute(context));
		
		future.addCallback((result) -> {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onSuccess");
			}
			
			context.setEndedAt(Instant.now());
			context.setStatus(BatchStatus.COMPLETED);
		}, (ex) -> {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onFailure");
			}
			context.setException(ex);
			context.setEndedAt(Instant.now());
			context.setStatus(BatchStatus.FAILED);
		});
	
		return future;
	}

}
