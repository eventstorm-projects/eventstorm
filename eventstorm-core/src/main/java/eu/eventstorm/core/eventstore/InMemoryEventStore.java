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

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventData;
import eu.eventstorm.core.EventStore;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<AggregateId, List<Event<?>>>> map = new HashMap<>();

	private final List<Event<?>> events = new LinkedList<>();

	@Override
	public Stream<Event<? extends EventData>> readStream(String stream, AggregateId aggregateId) {

		Map<AggregateId, List<Event<?>>> eventsByType = map.get(stream);

		if (eventsByType == null) {
			return null;
		}

		List<Event<?>> events = eventsByType.get(aggregateId);

		if (events == null) {
			return null;
		}

		return events.stream();
	}

	@Override
	public <T extends EventData> Event<T> appendToStream(String stream, AggregateId id, T data) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}] with data[{}]", stream, id, data);
		}

		Event<T> event =  new Event<T>(id, stream,  OffsetDateTime.now(), 0, data);
		 
		this.events.add(event);

		Map<AggregateId, List<Event<?>>> eventsByType = this.map.get(stream);

		if (eventsByType == null) {
			eventsByType = new HashMap<>();
			this.map.put(stream, eventsByType);
		}

		List<Event<?>> events = eventsByType.get(id);

		if (events == null) {
			events = new ArrayList<>();
			eventsByType.put(id, events);
		}

		events.add(event);

		return event;
	}

}
