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
import eu.eventstorm.core.impl.EventBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<AggregateId, List<Event<? extends EventPayload>>>> map = new HashMap<>();

	private final List<Event<? extends EventPayload>> allEvents = new LinkedList<>();

	@Override
	public Stream<Event<? extends EventPayload>> readStream(String streamId, AggregateId aggregateId) {

		Map<AggregateId, List<Event<? extends EventPayload>>> stream = map.get(streamId);

		if (stream == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, ImmutableMap.of("stream", streamId));
		}

		List<Event<? extends EventPayload>> events = stream.get(aggregateId);

		if (events == null) {
			return Stream.empty();
		}

		return events.stream();
	}

	@Override
	public <T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}] with data[{}]", aggregateType, id, payload);
		}

		// @formatter:off
		Event<T> event =  new EventBuilder<T>()
				.aggregateId(id)
				.aggreateType(aggregateType)
				.timestamp(OffsetDateTime.now())
				.revision(1)
				.payload(payload)
				.build();
				
		// @formatter:on

		this.allEvents.add(event);

		this.map.computeIfAbsent(aggregateType, key -> new HashMap<>()).computeIfAbsent(id, key -> new ArrayList<>()).add(event);

		return event;
	}

}
