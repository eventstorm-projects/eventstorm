package eu.eventstorm.eventstore.memory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.util.Strings;
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
	public Stream<Event> readRawStream(String stream, String streamId) {
		return readStream(stream, streamId);
	}

	@Override
	public <T extends Message> Event appendToStream(EventCandidate<T> candidate, String correlation) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store to [{}] with Id [{}]", candidate.getStream(), candidate.getStreamId());
		}

		Map<String, List<Event>> mapType = this.map.get(candidate.getStream());
		
		int revision = 1;
		
		if (mapType == null) {
			mapType = new HashMap<>();
			map.put(candidate.getStream(), mapType);
		} 
		
		List<Event> events = mapType.get(candidate.getStreamId());
		if (events == null) {
			events = new ArrayList<>();
			mapType.put(candidate.getStreamId(), events);
		} else {
			revision = events.get(events.size() - 1).getRevision() + 1;
		}
		
		// @formatter:off
		Event.Builder builder = Event.newBuilder()
				.setStreamId(candidate.getStreamId())
				.setStream(candidate.getStream())
				.setTimestamp(OffsetDateTime.now().toString())
				.setRevision(revision)
				.setData(Any.pack(candidate.getMessage(),this.eventStoreProperties.getEventDataTypeUrl() + "/" + candidate.getStream() + "/"))
				;

		if (!Strings.isEmpty(correlation)) {
			builder.setCorrelation(correlation);
		}

		Event event = builder.build();
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