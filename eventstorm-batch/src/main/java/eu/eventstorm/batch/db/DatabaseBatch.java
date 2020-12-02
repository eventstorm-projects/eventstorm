package eu.eventstorm.batch.db;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.google.common.base.Throwables;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.batch.BatchExecutor;
import eu.eventstorm.batch.BatchJob;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.type.Jsons;
import eu.eventstorm.sql.util.TransactionTemplate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseBatch implements Batch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBatch.class);
	
	private final BatchExecutor batchExecutor;
	private final ApplicationContext applicationContext;
	private final Database database;
	private final DatabaseExecutionRepository repository;
	private final TransactionTemplate template;
	private final JsonFormat.Printer jsonPrinter;
	
	public DatabaseBatch(ApplicationContext applicationContext, BatchExecutor batchExecutor, Database database, DatabaseExecutionRepository repository,
	        TypeRegistry registry) {
		this.applicationContext = applicationContext;
		this.batchExecutor = batchExecutor;
		this.database = database;
		this.repository = repository;
		this.template = new TransactionTemplate(database.transactionManager());
		this.jsonPrinter = JsonFormat.printer().omittingInsignificantWhitespace().usingTypeRegistry(registry).includingDefaultValueFields();
	}

	@Override
	public Event push(EventCandidate<BatchJobCreated> candidate) {
		
		BatchJob batchJob = this.applicationContext.getBean(candidate.getMessage().getName(), BatchJob.class);
			
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("BatchJob =[{}]", batchJob);
		}
		
		Event event = Event.newBuilder()
				.setStreamId(candidate.getStreamId())
				.setStream(candidate.getStream())
				//.setCorrelation(UUID.newBuilder().setLeastSigBits(correlation.getUuid().getLeastSignificantBits()).setMostSigBits(correlation.getUuid().getMostSignificantBits()))
				.setRevision(1)
				.setTimestamp(OffsetDateTime.now().toString())
				.setData(Any.pack(candidate.getMessage(),candidate.getStream()))
			.build();	
			
		DatabaseExecution batchExecution = new DatabaseExecutionBuilder()
				.withName(candidate.getStream())
				.withStatus((byte)BatchStatus.STARTING.ordinal())
				.withEvent(toJson(candidate.getMessage()))
				.withUuid(candidate.getStreamId())
				.withStartedAt(Timestamp.from(Instant.now()))
				.withCreatedBy(candidate.getMessage().getCreatedBy())
				.withLog(Jsons.createMap())
				.build();
			
		this.template.executeWithReadWrite(() -> repository.insert(batchExecution));
		
		DatabaseBatchJobContext context = new DatabaseBatchJobContext(database, batchExecution, candidate.getMessage());
		
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
			template.executeWithReadWrite(() -> repository.update(context.getDatabaseExecution()));
		}
		
		@Override
		public void onFailure(Throwable ex) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onFailure()", ex);
			}
			context.log("onFailure.message", ex.getMessage());
			context.log("onFailure.exception", Throwables.getStackTraceAsString(ex));
			template.executeWithReadWrite(() -> repository.update(context.getDatabaseExecution()));
		}
	}
	
	private String toJson(BatchJobCreated batchJobCreated) {
		try {
			return jsonPrinter.print(batchJobCreated);
		} catch (InvalidProtocolBufferException cause) {
			throw new IllegalStateException(cause);
		}
	}
	
}
