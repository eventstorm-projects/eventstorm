package eu.eventstorm.batch.memory;

import static java.util.UUID.randomUUID;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import eu.eventstorm.batch.file.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.google.protobuf.Any;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.batch.BatchExecutor;
import eu.eventstorm.batch.BatchJob;
import eu.eventstorm.batch.BatchJobContext;
import eu.eventstorm.batch.BatchResource;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryBatch implements Batch {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryBatch.class);

	private final ConcurrentLinkedQueue<InMemoryBatchJobContext> history = new ConcurrentLinkedQueue<>();
	
	private final BatchExecutor batchExecutor;
	private final ApplicationContext applicationContext;
	private final FileResource fileResource;

	public InMemoryBatch(ApplicationContext applicationContext, BatchExecutor batchExecutor,FileResource fileResource) {
		this.applicationContext = applicationContext;
		this.batchExecutor = batchExecutor;
		this.fileResource = fileResource;
	}

	@Override
	public Event push(EventCandidate<BatchJobCreated> candidate) {
		
		String correlation = randomUUID().toString();
		
		BatchJob batchJob = this.applicationContext.getBean(candidate.getMessage().getName(), BatchJob.class);
			
		Event event = Event.newBuilder()
				.setStreamId(candidate.getStreamId())
				.setStream(candidate.getStream())
				.setCorrelation(correlation)
				.setRevision(1)
				.setTimestamp(OffsetDateTime.now().toString())
				.setData(Any.pack(candidate.getMessage(),candidate.getStream()))
			.build();	
		
		InMemoryBatchJobContext context = new InMemoryBatchJobContext(candidate.getMessage());
		
		batchExecutor.submit(batchJob, context).addCallback(new InMemoryListenableFutureCallback<>(context));				
						
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("submitted");
		}
		
		return event;
	}

	private class InMemoryListenableFutureCallback<V> implements ListenableFutureCallback<V> {
		
		private final InMemoryBatchJobContext context;
		
		private InMemoryListenableFutureCallback(InMemoryBatchJobContext context) {
			this.context = context;
		}

		@Override
		public void onSuccess(V result) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onSuccess()");
			}
			
			InMemoryBatch.this.history.add(context);
		}
		
		@Override
		public void onFailure(Throwable throwable) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onFailure()", throwable);
			}
			
			InMemoryBatch.this.history.add(context);
		}
	}

	
	private final class InMemoryBatchJobContext implements BatchJobContext {
		
		private final BatchJobCreated batchJobCreated;
		private Instant endedAt;
		private BatchStatus batchStatus;
		
		private InMemoryBatchJobContext(BatchJobCreated batchJobCreated) {
			this.batchJobCreated = batchJobCreated;
		}
		
		@Override
		public void setEndedAt(Instant endedAt) {
			this.endedAt = endedAt;
		}
		
		@Override
		public void setStatus(BatchStatus batchStatus) {
			this.batchStatus = batchStatus;
		}

		@Override
		public BatchJobCreated getBatchJobCreated() {
			return this.batchJobCreated;
		}

		@Override
		public BatchResource getResource(String uuid) {
			return () -> {
				try {
					return Files.newInputStream(fileResource.get(uuid));
				} catch (IOException cause) {
					throw new RuntimeException(cause);
				}
			};
		}

		@Override
		public void setException(Throwable ex) {
			LOGGER.error("InMemoryBatchJobContext.setException", ex);
		}

		@Override
		public void log(String key, Object value) {
			LOGGER.info("info (key={},object{})", key, value);
		}

		@Override
		public void log(String key, Map<String, Object> value) {
			LOGGER.info("info(key={},map={})", key, value);
		}

		@Override
		public void log(String key, List<Object> value) {
			LOGGER.info("info(key={},list={})", key, value);
		}
		
	}
}