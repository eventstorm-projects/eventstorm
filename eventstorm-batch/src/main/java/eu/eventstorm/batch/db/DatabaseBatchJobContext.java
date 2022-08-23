package eu.eventstorm.batch.db;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import eu.eventstorm.util.FastByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.batch.BatchJobContext;
import eu.eventstorm.batch.BatchResource;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.util.TransactionTemplate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseBatchJobContext implements BatchJobContext {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseBatchJobContext.class);

	private final DatabaseExecution databaseExecution;
	private final TransactionTemplate transactionTemplate;
	private final DatabaseResourceRepository repository;
	private final BatchJobCreated event;
	
	public DatabaseBatchJobContext(Database database, DatabaseExecution databaseExecution, BatchJobCreated event) {
		this.databaseExecution = databaseExecution;
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
		this.repository = new DatabaseResourceRepository(database);
		this.event = event;
	}

	public DatabaseExecution getDatabaseExecution() {
		return databaseExecution;
	}

	@Override
	public void setEndedAt(Instant endedAt) {
		this.databaseExecution.setEndedAt(Timestamp.from(endedAt));
	}

	@Override
	public void setStatus(BatchStatus status) {
		this.databaseExecution.setStatus(status.name());
	}
	
	@Override
	public void setException(Throwable ex) {
		LOGGER.info("Batch failed", ex);
		Map<String,String> exceptionMap = new HashMap<>();
		exceptionMap.put("message", ex.getMessage());
		exceptionMap.put("cause", Throwables.getStackTraceAsString(ex));
		this.databaseExecution.getLog().asMap().put("exception", exceptionMap);
	}

	@Override
	public BatchJobCreated getBatchJobCreated() {
		return this.event;
	}
	
	@Override
	public BatchResource getResource(String uuid) {
		return new DatabaseBatchResourceWrapper(uuid);
	}

	@Override
	public void log(String key, Object value) {
		this.databaseExecution.getLog().asMap().put(key, value);
	}

	@Override
	public void log(String key, Map<String, Object> value) {
		this.databaseExecution.getLog().asMap().put(key, value);
	}
	
	@Override
	public void log(String key, List<Object> value) {
		this.databaseExecution.getLog().asMap().put(key, value);
	}

	private final class DatabaseBatchResourceWrapper implements BatchResource {
		
		private final String uuid;
		
		private DatabaseBatchResourceWrapper(String uuid) {
			this.uuid = uuid;
		}

		@Override
		public InputStream getContent() {
			return transactionTemplate.executeWithReadOnly(() -> new FastByteArrayInputStream(repository.findById(uuid).getContent()));
		}
		
	}

}