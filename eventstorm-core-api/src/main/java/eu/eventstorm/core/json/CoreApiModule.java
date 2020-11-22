package eu.eventstorm.core.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.core.Event;


@SuppressWarnings("serial")
public final class CoreApiModule extends SimpleModule {

	public CoreApiModule(TypeRegistry registry) {
		super();
		addSerializer(Event.class, new EventStdSerializer(registry));
	}

	
}
