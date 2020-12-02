package eu.eventstorm.batch.db;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

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
		this.databaseExecution.setStatus((byte)status.ordinal()); 
	}
	
	@Override
	public void setException(Throwable ex) {
		LOGGER.info("Batch failed", ex);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		this.databaseExecution.getLog().asMap().put("exception",  sw.toString());
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
			return transactionTemplate.executeWithReadOnly(() -> {
				try {
					return repository.findById(uuid).getContent().getBinaryStream();
				} catch (SQLException cause) {
					throw new IllegalStateException(cause);
				}
			});
		}
		
	}

}