package eu.eventstorm.core;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	Stream<Event> readStream(String stream, AggregateId aggregateId);

	Event appendToStream(String stream, AggregateId id, EventPayload payload);

}