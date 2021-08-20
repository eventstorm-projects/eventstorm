package eu.eventstorm.eventstore;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.AbstractMessage;

import com.google.protobuf.Message;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStoreClient {

	Event appendToStream(EventCandidate<? extends Message> candidate);
	
	Stream<Event> appendToStream(ImmutableList<EventCandidate<? extends Message>>candidates);
	
	Stream<Event> readStream(String stream, String streamId);

}
