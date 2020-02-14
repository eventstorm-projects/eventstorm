package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	Stream<Event<EventPayload>> readStream(String aggregateType, AggregateId aggregateId);

	<T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload);

}