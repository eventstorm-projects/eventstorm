package eu.eventstorm.batch.db;

import static java.util.UUID.randomUUID;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.google.protobuf.Any;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.batch.BatchExecutor;
import eu.eventstorm.batch.BatchJob;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.UUID;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseBatch implements Batch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBatch.class);
	
	private final BatchExecutor batchExecutor;
	
	private final ApplicationContext applicationContext;
	
	private final Database database;
	
	private final DatabaseExecutionRepository repository;
	
	public DatabaseBatch(ApplicationContext applicationContext, BatchExecutor batchExecutor, Database database, DatabaseExecutionRepository repository) {
		this.applicationContext = applicationContext;
		this.batchExecutor = batchExecutor;
		this.database = database;
		this.repository = repository;
	}

	@Override
	public Event push(EventCandidate<BatchJobCreated> candidate) {
		
		java.util.UUID correlation = randomUUID();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("correlation id =[{}]", correlation);
		}
		
		BatchJob batchJob = this.applicationContext.getBean(candidate.getMessage().getName(), BatchJob.class);
			
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("BatchJob =[{}]", batchJob);
		}
		
		Event event = Event.newBuilder()
				.setStreamId(candidate.getStreamId().toStringValue())
				.setStream(candidate.getStream())
				.setCorrelation(UUID.newBuilder().setLeastSigBits(correlation.getLeastSignificantBits()).setMostSigBits(correlation.getMostSignificantBits()))
				.setRevision(1)
				.setTimestamp(OffsetDateTime.now().toString())
				.setData(Any.pack(candidate.getMessage(),candidate.getStream()))
			.build();	
			
		DatabaseExecution batchExecution = new DatabaseExecutionBuilder()
				.withName(candidate.getStream())
				.withStatus((byte)BatchStatus.STARTING.ordinal())
				.withResources(database.dialect().createJson(candidate.getMessage().getUuidList()))
				.withUuid(correlation.toString())
				.withStartedAt(Timestamp.from(Instant.now()))
				.build();
			
		DatabaseBatchJobContext context = new DatabaseBatchJobContext(database, batchExecution);
		
		batchExecutor.submit(batchJob, context).addCallback(new DatabaseBatchListenableFutureCallback<>(context));				
						
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("submitted");
		}
		
		return event;
	}

	private class DatabaseBatchListenableFutureCallback<V> implements ListenableFutureCallback<V> {
		
		private final DatabaseBatchJobContext context;
		
		private DatabaseBatchListenableFutureCallback(DatabaseBatchJobContext context) {
			this.context = context;
		}

		@Override
		public void onSuccess(V result) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onSuccess()");
			}
			
			try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
				repository.update(context.getDatabaseExecution());
			}
			
		}
		
		@Override
		public void onFailure(Throwable ex) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onFailure()", ex);
			}
			
			try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
				// TODO => exception to json in log
				//context.getBatchExecution().setLog(json);
				repository.update(context.getDatabaseExecution());
			}

		}
	}
}
