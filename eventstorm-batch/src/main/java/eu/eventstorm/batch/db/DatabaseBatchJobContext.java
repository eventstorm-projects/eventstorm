package eu.eventstorm.batch.db;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.batch.BatchJobContext;
import eu.eventstorm.batch.BatchResource;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.type.JsonList;
import eu.eventstorm.sql.util.TransactionTemplate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseBatchJobContext implements BatchJobContext {

	private final DatabaseExecution databaseExecution;
	private final TransactionTemplate transactionTemplate;
	private final DatabaseResourceRepository repository;
	
	public DatabaseBatchJobContext(Database database, DatabaseExecution databaseExecution) {
		this.databaseExecution = databaseExecution;
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
		this.repository = new DatabaseResourceRepository(database);
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
	public List<BatchResource> getResources() {
		ImmutableList.Builder<BatchResource> builder = ImmutableList.builder();		
		JsonList list = this.databaseExecution.getResources().asList();
		for (int i = 0; i < list.size(); i++) {
			builder.add(new DatabaseBatchResourceWrapper(list.get(i, String.class)));
		}
		return builder.build();
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