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
import com.google.protobuf.Any;
import com.google.protobuf.Message;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreException;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.Statistics;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStore.class);

	private final Map<String, Map<String, List<Event>>> map = new HashMap<>();

	private final List<Event> allEvents = new LinkedList<>();
	
	private final EventStoreProperties eventStoreProperties;
	
	public InMemoryEventStore(EventStoreProperties eventStoreProperties) {
		this.eventStoreProperties = eventStoreProperties;
	}

	@Override
	public Stream<Event> readStream(String streamName, String streamId) {
	
		Map<String, List<Event>> stream = map.get(streamName);

		if (stream == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, ImmutableMap.of("stream", streamName));
		}

		List<Event> events = stream.get(streamId);

		if (events == null) {
			return Stream.empty();
		}

		return events.stream();
	}
	 
	@Override
	public Event appendToStream(String stream, String streamId, String correlation, Message message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}]", stream, streamId);
		}

		Map<String, List<Event>> mapType = this.map.get(stream);
		
		int revision = 1;
		
		if (mapType == null) {
			mapType = new HashMap<>();
			map.put(stream, mapType);
		} 
		
		List<Event> events = mapType.get(streamId);
		if (events == null) {
			events = new ArrayList<>();
			mapType.put(streamId, events);
		} else {
			revision = events.get(events.size() - 1).getRevision() + 1;
		}
		
		// @formatter:off
		Event event = Event.newBuilder()
				.setStreamId(streamId)
				.setStream(stream)
				.setTimestamp(OffsetDateTime.now().toString())
				.setRevision(revision)
				.setCorrelation(correlation)
				.setData(Any.pack(message,this.eventStoreProperties.getEventDataTypeUrl() + "/" + stream + "/"))
				.build();
		// @formatter:on

		this.allEvents.add(event);
		events.add(event);
		return event;
	}

	@Override
	public Statistics stat(String stream) {
		// TODO Auto-generated method stub
		return null;
	}

}