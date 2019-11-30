package eu.eventstorm.core.eventstore;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.impl.Events;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<AggregateId, List<Event>>> map = new HashMap<>();

	private final List<Event> events = new LinkedList<>();

	@Override
	public Stream<Event> readStream(String streamId, AggregateId aggregateId) {

		Map<AggregateId, List<Event>> stream = map.get(streamId);

		if (stream == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, ImmutableMap.of("stream", streamId));
		}

		List<Event> events = stream.get(aggregateId);

		if (events == null) {
			return Stream.empty();
		}

		return events.stream();
	}

	@Override
	public Event appendToStream(String aggregateType, AggregateId id, EventPayload data) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}] with data[{}]", aggregateType, id, data);
		}

		Event event = Events.newEvent(id, aggregateType, OffsetDateTime.now(), 0, data);

		this.events.add(event);

		Map<AggregateId, List<Event>> eventsByType = this.map.get(aggregateType);

		if (eventsByType == null) {
			eventsByType = new HashMap<>();
			this.map.put(aggregateType, eventsByType);
		}

		List<Event> events = eventsByType.get(id);

		if (events == null) {
			events = new ArrayList<>();
			eventsByType.put(id, events);
		}

		events.add(event);

		return event;
	}

}
