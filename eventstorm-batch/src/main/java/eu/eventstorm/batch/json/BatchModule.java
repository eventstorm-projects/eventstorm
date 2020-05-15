package eu.eventstorm.batch.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.cqrs.batch.BatchJobCreated;

@SuppressWarnings("serial")
public final class BatchModule extends SimpleModule {

	public BatchModule(TypeRegistry registry) {
		super();
		addSerializer(BatchJobCreated.class, new BatchJobCreatedSerializer());
	}
	
}
