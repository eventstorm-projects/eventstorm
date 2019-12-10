package eu.eventstorm.core;

import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventPayloadSchemaRegistry {

	EventPayloadSchema getSchema(EventPayload payload);

	<T extends EventPayload>Deserializer<T> getDeserializer(String schema, int schemaVersion);
	
	Serializer<EventPayload> getSerializer(String schema, int schemaVersion);

}
