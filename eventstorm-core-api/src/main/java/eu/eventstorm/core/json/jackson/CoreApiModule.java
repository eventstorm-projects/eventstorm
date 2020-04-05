package eu.eventstorm.core.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.core.id.StringStreamId;


@SuppressWarnings("serial")
public final class CoreApiModule extends SimpleModule {

	public CoreApiModule() {
		super();
		addSerializer(StringStreamId.class, new StringAggregateIdSerializer());
	}

	
}
