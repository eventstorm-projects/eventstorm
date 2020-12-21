package eu.eventstorm.eventstore.memory;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.eventstore.EventStoreException.PARAM_STREAM;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.AbstractMessage;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.EventStoreClient;
import eu.eventstorm.eventstore.EventStoreException;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamEventDefinition;
import eu.eventstorm.eventstore.StreamManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventStoreClient implements EventStoreClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventStoreClient.class);
	
	private final InMemoryEventStore inMemoryEventStore;
	private final StreamManager streamManager;
	
	public InMemoryEventStoreClient(StreamManager streamManager, InMemoryEventStore inMemoryEventStore) {
		this.streamManager = streamManager;
		this.inMemoryEventStore = inMemoryEventStore;
	}


	@Override
	public Event appendToStream(String stream, String streamId, AbstractMessage message) {
		
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("appendToStream({},{},{})", stream, streamId, message);
		}
		
		return appendToStream(stream, streamId, UUID.randomUUID(), message);
	}
	

	@Override
	public Stream<Event> readStream(String stream, String streamId) {
		return inMemoryEventStore.readStream(stream, streamId);
	}


	@Override
	public Stream<Event> appendToStream(ImmutableList<EventCandidate<?>> candidates) {
		List<Event> events = new ArrayList<>(candidates.size());
		UUID correlation = UUID.randomUUID();
		
		for (EventCandidate<?> candidate : candidates) {
			events.add(appendToStream(candidate.getStream(), candidate.getStreamId(), correlation, candidate.getMessage()));
		}
		
		return events.stream();
	}


	private Event appendToStream(String stream, String streamId, UUID uuid, AbstractMessage message) {
		
		StreamDefinition sd = this.streamManager.getDefinition(stream);
		
		if (sd == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, of(PARAM_STREAM, stream));
		}
		
		// if sepd not found => exception.
		StreamEventDefinition sepd = sd.getStreamEventDefinition(message.getDescriptorForType().getName());
		
		return this.inMemoryEventStore.appendToStream(stream, streamId, UUID.randomUUID().toString(), message);
	}
	

}
