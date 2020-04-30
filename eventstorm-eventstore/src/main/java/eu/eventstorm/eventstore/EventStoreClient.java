package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import com.google.protobuf.AbstractMessage;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStoreClient {

	<T extends AbstractMessage> Event appendToStream(String stream, StreamId streamId, T evantPayload);

	Stream<Event> readStream(String stream, StreamId streamId);

}
