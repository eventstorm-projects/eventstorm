package eu.eventstorm.core.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.id.StringStreamId;


@SuppressWarnings("serial")
public final class CoreApiModule extends SimpleModule {

	public CoreApiModule(TypeRegistry registry) {
		super();
		addSerializer(StringStreamId.class, new StringAggregateIdSerializer());
		addSerializer(Event.class, new EventStdSerializer(registry));
	}

	
}
