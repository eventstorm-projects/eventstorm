package eu.eventstorm.batch.memory;

import static java.util.UUID.randomUUID;

import java.sql.Timestamp;
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
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.batch.db.BatchExecution;
import eu.eventstorm.batch.db.BatchExecutionBuilder;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.UUID;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

public final class InMemoryBatch implements Batch {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryBatch.class);

	private final ConcurrentSkipListSet<BatchExecution> history = new ConcurrentSkipListSet<>();
	
	private final BatchExecutor batchExecutor;
	
	private final ApplicationContext applicationContext;
	
	public InMemoryBatch(ApplicationContext applicationContext, BatchExecutor batchExecutor) {
		this.applicationContext = applicationContext;
		this.batchExecutor = batchExecutor;
	}


	@Override
	public Event push(EventCandidate<BatchJobCreated> candidate) {
		
		java.util.UUID correlation = randomUUID();
		
		BatchJob batchJob = this.applicationContext.getBean(candidate.getMessage().getName(), BatchJob.class);
			
		Event event = Event.newBuilder()
				.setStreamId(candidate.getStreamId().toStringValue())
				.setStream(candidate.getStream())
				.setCorrelation(UUID.newBuilder().setLeastSigBits(correlation.getLeastSignificantBits()).setMostSigBits(correlation.getMostSignificantBits()))
				.setRevision(1)
				.setTimestamp(OffsetDateTime.now().toString())
				.setData(Any.pack(candidate.getMessage(),candidate.getStream()))
			.build();	
			
		BatchExecution batchExecution = new BatchExecutionBuilder()
				.withName(candidate.getStream())
				.withStatus((byte)BatchStatus.STARTING.ordinal())
				.withResource(candidate.getMessage().getUuid())
				.withUuid(correlation.toString())
				.withStartedAt(Timestamp.from(Instant.now()))
				.build();
			
		BatchJobContext context = new BatchJobContext() {
			@Override
			public BatchExecution getBatchExecution() {
				return batchExecution;
			}
		};
		
		batchExecutor.submit(batchJob, context).addCallback(new InMemoryListenableFutureCallback<>(context));				
						
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("submitted");
		}
		
		return event;
	}

	private class InMemoryListenableFutureCallback<V> implements ListenableFutureCallback<V> {
		
		private final BatchJobContext context;
		
		private InMemoryListenableFutureCallback(BatchJobContext context) {
			this.context = context;
		}

		@Override
		public void onSuccess(V result) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onSuccess()");
			}
			
			InMemoryBatch.this.history.add(context.getBatchExecution());
		}
		
		@Override
		public void onFailure(Throwable ex) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onFailure()");
			}
			
			InMemoryBatch.this.history.add(context.getBatchExecution());
		}
	}

}