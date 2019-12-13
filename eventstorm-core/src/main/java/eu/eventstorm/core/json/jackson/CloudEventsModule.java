package eu.eventstorm.core.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.eventstorm.core.cloudevent.CloudEvent;


@SuppressWarnings("serial")
public final class CloudEventsModule extends SimpleModule {

	public CloudEventsModule() {
		super();
		addSerializer(CloudEvent.class, new CloudEventSerializer());
		addDeserializer(CloudEvent.class, new CloudEventDeserializer());
	}

	
}
