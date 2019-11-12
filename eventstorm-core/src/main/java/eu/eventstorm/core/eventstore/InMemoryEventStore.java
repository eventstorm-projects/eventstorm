package eu.eventstorm.core.eventstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.EventStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<AggregateId, List<Event>>> map = new HashMap<>();
	
	private final List<MemoryStoreItem> events = new LinkedList<>();
	
	@Override
	public EventStream load(AggregateId aggregateId) {
		
		Map<AggregateId, List<Event>> eventsByType = map.get(aggregateId.name());
		
		if (eventsByType == null) {
			return null;
		}
		
		List<Event> events = eventsByType.get(aggregateId);
		
		if (events == null) {
			return null;
		}
		
		return null;
	}

	@Override
	public void store(AggregateId id, Event event) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store [{}] - event [{}]", id, event);
		}
		
		this.events.add(new MemoryStoreItem(id, event));
		
		Map<AggregateId, List<Event>> eventsByType = this.map.get(id.name());

		if (eventsByType == null) {
			eventsByType = new HashMap<>();
			this.map.put(id.name(), eventsByType);
		}

		List<Event> events = eventsByType.get(id);
		
		if (events == null) {
			events = new ArrayList<>();
			eventsByType.put(id, events);
		}
		
		events.add(event);
	}

	
	private static class MemoryStoreItem {
		
		private final AggregateId id;
		private final Event event;

		public MemoryStoreItem(AggregateId id, Event event) {
			this.id = id;
			this.event = event;
		}

		public AggregateId getId() {
			return id;
		}

		public Event getEvent() {
			return event;
		}
		
	}
	
}
