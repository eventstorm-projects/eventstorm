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

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBuilder;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.id.StreamIds;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreException;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamEvantPayloadDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<String, List<Event<?>>>> map = new HashMap<>();

	private final List<Event<? extends EventPayload>> allEvents = new LinkedList<>();

	@Override
	public Stream<Event<?>> readStream(StreamDefinition definition, String streamId) {
	
		Map<String, List<Event<?>>> stream = map.get(definition.getName());

		if (stream == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, ImmutableMap.of("stream", definition.getName()));
		}

		List<Event<?>> events = stream.get(streamId);

		if (events == null) {
			return Stream.empty();
		}

		return events.stream();
	}
	
	@Override
	public	<T extends EventPayload> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, byte[] data) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}]", sepd, streamId);
		}

		Map<String, List<Event<?>>> mapType = this.map.get(sepd.getStream());
		
		int revision = 1;
		
		if (mapType == null) {
			mapType = new HashMap<>();
			map.put(sepd.getStream(), mapType);
		} 
		
		List<Event<?>> events = mapType.get(streamId);
		if (events == null) {
			events = new ArrayList<>();
			mapType.put(streamId, events);
		} else {
			revision = events.get(events.size() - 1).getRevision() + 1;
		}
		
		// @formatter:off
		Event<T> event =  new EventBuilder<T>()
				.withStreamId(StreamIds.from(streamId))
				.withStream(sepd.getStream())
				.withTimestamp(OffsetDateTime.now())
				.withRevision(revision)
				.withPayload(sepd.getPayloadDeserializer().deserialize(data))
				.build();
		// @formatter:on

		this.allEvents.add(event);
		events.add(event);
		return event;
	}

	@Override
	public <T extends EventPayload> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, T eventPayload) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}]", sepd, streamId);
		}

		Map<String, List<Event<?>>> mapType = this.map.get(sepd.getStream());
		
		int revision = 1;
		
		if (mapType == null) {
			mapType = new HashMap<>();
			map.put(sepd.getStream(), mapType);
		} 
		
		List<Event<?>> events = mapType.get(streamId);
		if (events == null) {
			events = new ArrayList<>();
			mapType.put(streamId, events);
		} else {
			revision = events.get(events.size() - 1).getRevision() + 1;
		}
		
		// @formatter:off
		Event<T> event =  new EventBuilder<T>()
				.withStreamId(StreamIds.from(streamId))
				.withStream(sepd.getStream())
				.withTimestamp(OffsetDateTime.now())
				.withRevision(revision)
				.withPayload(eventPayload)
				.build();
		// @formatter:on

		this.allEvents.add(event);
		events.add(event);
		return event;
	}

	public <T extends EventPayload> Event<T> appendToStream(StreamDefinition definition, String streamId, T eventPayload) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("appendToStream to [{}] with Id [{}]", definition, streamId);
		}

		Map<String, List<Event<?>>> mapType = this.map.get(definition.getName());
		
		int revision = 1;
		
		if (mapType == null) {
			mapType = new HashMap<>();
			map.put(definition.getName(), mapType);
		} 
		
		List<Event<?>> events = mapType.get(streamId);
		if (events == null) {
			events = new ArrayList<>();
			mapType.put(streamId, events);
		} else {
			revision = events.get(events.size() - 1).getRevision() + 1;
		}
		
		// @formatter:off
		Event<T> event =  new EventBuilder<T>()
				.withStreamId(StreamIds.from(streamId))
				.withStream(definition.getName())
				.withTimestamp(OffsetDateTime.now())
				.withRevision(revision)
				.withPayload(eventPayload)
				.build();
		// @formatter:on

		this.allEvents.add(event);
		events.add(event);
		return event;
	}


}