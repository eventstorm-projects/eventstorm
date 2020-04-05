package eu.eventstorm.eventstore;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;

public interface StreamEvantPayloadDefinition<T extends EventPayload> {
	
	String getStream();

	Class<T> getEventPayloadClass();
	
	Deserializer<T> getPayloadDeserializer();

	Serializer<T> getPayloadSerializer();
	
	String getPayloadType();

}
