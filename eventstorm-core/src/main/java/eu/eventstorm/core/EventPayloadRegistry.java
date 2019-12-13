package eu.eventstorm.core;

import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventPayloadRegistry {

	String getPayloadType(EventPayload payload);
	
	byte getPayloadVersion(EventPayload payload);

	<T extends EventPayload> Deserializer<T> getDeserializer(String payloadType);
	
	Serializer<EventPayload> getSerializer(String payloadType);

}
