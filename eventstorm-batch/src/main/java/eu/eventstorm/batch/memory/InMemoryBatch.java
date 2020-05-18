package eu.eventstorm.batch.memory;

import static java.util.UUID.randomUUID;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.batch.db.BatchExecution;
import eu.eventstorm.batch.db.BatchExecutionBuilder;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.UUID;

public final class InMemoryBatch implements Batch {

	private final ConcurrentSkipListSet<BatchExecution> history = new ConcurrentSkipListSet<>();
	
	private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	public InMemoryBatch() {
		this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		this.threadPoolTaskScheduler.setThreadNamePrefix("batch-");
		this.threadPoolTaskScheduler.setPoolSize(1);
		this.threadPoolTaskScheduler.initialize();
	}

	@Override
	public ImmutableList<Event> push(ImmutableList<EventCandidate> candidates) {
		
		java.util.UUID correlation = randomUUID();
		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		
		for (EventCandidate candidate : candidates) {
			
			Event event = Event.newBuilder()
					.setStreamId(candidate.getStreamId().toStringValue())
					.setStream(candidate.getStream())
					.setCorrelation(UUID.newBuilder().setLeastSigBits(correlation.getLeastSignificantBits()).setMostSigBits(correlation.getMostSignificantBits()))
					.setRevision(1)
					.setTimestamp(OffsetDateTime.now().toString())
					.setData(Any.pack(candidate.getMessage(),candidate.getStream()))
				.build();	
			
			builder.add(event);
			
			BatchExecution batchExecution = new BatchExecutionBuilder()
					.withName(candidate.getStream())
					.withStatus((byte)BatchStatus.STARTING.ordinal())
					.withUuid(correlation.toString())
					.build();
			
			this.threadPoolTaskScheduler.submit(new Runnable() {
				
				@Override
				public void run() {
					batchExecution.setStartedAt(new Timestamp(System.currentTimeMillis()));
					try {
						//	doJob();
					} finally {
						batchExecution.setEndedAt(new Timestamp(System.currentTimeMillis()));
					}

					LoggerFactory.getLogger(InMemoryBatch.class).info("---------------------------" + candidate.getStream());

					LoggerFactory.getLogger(InMemoryBatch.class).info("---------------------------" + candidate.getMessage());
					LoggerFactory.getLogger(InMemoryBatch.class).info("---------------------------" + batchExecution);
					
					
				}
				
			});
			
		}
		
		return builder.build();
	}

	

}