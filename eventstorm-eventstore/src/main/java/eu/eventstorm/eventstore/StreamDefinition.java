package eu.eventstorm.eventstore;

import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface StreamDefinition {
	
	String getName();

	<T extends EventPayload> StreamEvantPayloadDefinition<T> getStreamEvantPayloadDefinition(String payloadType);

	<T extends EventPayload> StreamEvantPayloadDefinition<T> getStreamEvantPayloadDefinition(T eventPayload);
	
}