package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import com.google.protobuf.AbstractMessage;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	//Event appendToStream(StreamEvantPayloadDefinition<AbstractMessage> sepd, String streamId, byte[] data);

	Event appendToStream(StreamEventDefinition sepd, String streamId, AbstractMessage message);
	
	Stream<Event> readStream(StreamDefinition definition, String streamId);

	Statistics stat(String stream);

}