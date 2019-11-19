package eu.eventstorm.core;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	Stream<Event<? extends EventData>> readStream(String stream, AggregateId aggregateId);

	<T extends EventData> Event<T> appendToStream(String stream, AggregateId id, T eventData);

}