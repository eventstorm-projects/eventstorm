package eu.eventstorm.batch.memory;

import static java.util.UUID.randomUUID;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableList;
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
	public Event push(String stream, String streamId, BatchJobCreated candidate) {
		
		java.util.UUID correlation = randomUUID();
		
		BatchJob batchJob = this.applicationContext.getBean(candidate.getName(), BatchJob.class);
			
		Event event = Event.newBuilder()
				.setStreamId(streamId)
				.setStream(stream)
				.setCorrelation(UUID.newBuilder().setLeastSigBits(correlation.getLeastSignificantBits()).setMostSigBits(correlation.getMostSignificantBits()))
				.setRevision(1)
				.setTimestamp(OffsetDateTime.now().toString())
				.setData(Any.pack(candidate,stream))
			.build();	
			
		BatchExecution batchExecution = new BatchExecutionBuilder()
				.withName(stream)
				.withStatus((byte)BatchStatus.STARTING.ordinal())
				.withUuid(correlation.toString())
				.build();
			
		BatchJobContext context = new BatchJobContext() {
			@Override
			public BatchExecution getBatchExecution() {
				return batchExecution;
			}
		};
		
		batchExecutor.submit(batchJob, context);					
						
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("submitted");
		}
		
		return event;
	}


}