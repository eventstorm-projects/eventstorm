package eu.eventstorm.core;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	Stream<Event<? extends EventPayload>> readStream(String aggregateType, AggregateId aggregateId);

	<T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload);

}