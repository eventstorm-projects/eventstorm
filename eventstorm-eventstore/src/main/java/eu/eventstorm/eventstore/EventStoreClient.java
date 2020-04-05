package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStoreClient {

	<T extends EventPayload> Event<T> appendToStream(String stream, StreamId streamId, T evantPayload);

	Stream<Event<?>> readStream(String stream, StreamId streamId);

}
