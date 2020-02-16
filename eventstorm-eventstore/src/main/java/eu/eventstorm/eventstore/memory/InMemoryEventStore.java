package eu.eventstorm.eventstore.memory;

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
import eu.eventstorm.core.EventBuilder;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<AggregateId, List<Event<EventPayload>>>> map = new HashMap<>();

	private final List<Event<? extends EventPayload>> allEvents = new LinkedList<>();

	@Override
	public Stream<Event<EventPayload>> readStream(String streamId, AggregateId aggregateId) {

		Map<AggregateId, List<Event<EventPayload>>> stream = map.get(streamId);

		if (stream == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, ImmutableMap.of("stream", streamId));
		}

		List<Event<EventPayload>> events = stream.get(aggregateId);

		if (events == null) {
			return Stream.empty();
		}

		return events.stream();
	}

	@SuppressWarnings("unchecked")
    @Override
	public <T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}] with data[{}]", aggregateType, id, payload);
		}

		// @formatter:off
		Event<T> event =  new EventBuilder<T>()
				.withAggregateId(id)
				.withAggreateType(aggregateType)
				.withTimestamp(OffsetDateTime.now())
				.withRevision(1)
				.withPayload(payload)
				.build();
				
		// @formatter:on

		this.allEvents.add(event);

		this.map.computeIfAbsent(aggregateType, key -> new HashMap<>()).computeIfAbsent(id, key -> new ArrayList<>())
		    .add((Event<EventPayload>) event);

		return event;
	}

}
