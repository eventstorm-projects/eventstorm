package eu.eventstorm.cloudevents.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.cloudevents.CloudEvent;


@SuppressWarnings("serial")
public final class CloudEventsModule extends SimpleModule {

	public CloudEventsModule(TypeRegistry registry) {
		super();
		addSerializer(CloudEvent.class, new CloudEventSerializer(registry));
		addDeserializer(CloudEvent.class, new CloudEventDeserializer());
	}

	
}
