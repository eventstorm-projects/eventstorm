package eu.eventstorm.core.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.core.Event;


@SuppressWarnings("serial")
public final class CloudEventsModule extends SimpleModule {

	public CloudEventsModule() {
		super();
		addSerializer(Event.class, new EventSerializer());
	}

	
}
