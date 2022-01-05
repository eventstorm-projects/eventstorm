package eu.eventstorm.batch.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.batch.rest.DatabaseResourceQuery;
import eu.eventstorm.batch.rest.DatabaseResourceQueryStdSerializer;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

public final class BatchModule extends SimpleModule {

	public BatchModule() {
		super();
		addSerializer(BatchJobCreated.class, new BatchJobCreatedSerializer());
		addSerializer(DatabaseResourceQuery.class, new DatabaseResourceQueryStdSerializer());
	}
	
}
