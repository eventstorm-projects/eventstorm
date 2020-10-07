package eu.eventstorm.batch.memory;

import static java.util.UUID.randomUUID;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentSkipListSet;

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

	private final ConcurrentSkipListSet<InMemoryBatchJobContext> history = new ConcurrentSkipListSet<>();
	
	private final BatchExecutor batchExecutor;
	
	private final ApplicationContext applicationContext;
	
	public InMemoryBatch(ApplicationContext applicationContext, BatchExecutor batchExecutor) {
		this.applicationContext = applicationContext;
		this.batchExecutor = batchExecutor;
	}

	@Override
	public Event push(EventCandidate<BatchJobCreated> candidate) {
		
		String correlation = randomUUID().toString();
		
		BatchJob batchJob = this.applicationContext.getBean(candidate.getMessage().getName(), BatchJob.class);
			
		Event event = Event.newBuilder()
				.setStreamId(candidate.getStreamId().toStringValue())
				.setStream(candidate.getStream())
				.setCorrelation(correlation)
				.setRevision(1)
				.setTimestamp(OffsetDateTime.now().toString())
				.setData(Any.pack(candidate.getMessage(),candidate.getStream()))
			.build();	
		
		InMemoryBatchJobContext context = new InMemoryBatchJobContext();
		
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
		public void onFailure(Throwable ex) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onFailure()");
			}
			
			InMemoryBatch.this.history.add(context);
		}
	}

	
	private static final class InMemoryBatchJobContext implements BatchJobContext {
		
		private Instant endedAt;
		private BatchStatus batchStatus;
		
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
			return null;
		}

		@Override
		public BatchResource getResource(String uuid) {
			return null;
		}
		
	}
}