package eu.eventstorm.eventstore.memory;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.eventstore.EventStoreException.PARAM_STREAM;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.StreamId;
import eu.eventstorm.eventstore.EventStoreClient;
import eu.eventstorm.eventstore.EventStoreException;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamEvantPayloadDefinition;
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
	public <T extends EventPayload> Event<T> appendToStream(String stream, StreamId streamId, T evantPayload) {
		
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("appendToStream({},{},{})", stream, streamId, evantPayload);
		}
		
		StreamDefinition sd = this.streamManager.getDefinition(stream);
		
		if (sd == null) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, of(PARAM_STREAM, stream));
		}
		
		// if sepd not found => exception.
		StreamEvantPayloadDefinition<T> sepd = sd.getStreamEvantPayloadDefinition(evantPayload);
		
		return this.inMemoryEventStore.appendToStream(sepd, streamId.toStringValue(), evantPayload);
	}


	@Override
	public Stream<Event<?>> readStream(String stream, StreamId streamId) {
		return inMemoryEventStore.readStream(streamManager.getDefinition(stream), streamId.toStringValue());
	}

}
