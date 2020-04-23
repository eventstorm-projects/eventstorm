package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	<T extends EventPayload> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, byte[] data);

	<T extends EventPayload> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, T eventPayload);
	
	Stream<Event<EventPayload>> readStream(StreamDefinition definition, String streamId);

	Statistics stat(String stream);

}