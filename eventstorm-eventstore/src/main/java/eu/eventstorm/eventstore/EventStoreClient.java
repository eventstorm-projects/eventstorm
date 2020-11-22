package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.AbstractMessage;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStoreClient {

	Event appendToStream(String stream, String streamId, AbstractMessage evantPayload);
	
	Stream<Event> appendToStream(ImmutableList<EventCandidate<?>>candidates);
	
	Stream<Event> readStream(String stream, String streamId);

}
