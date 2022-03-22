package eu.eventstorm.eventstore;

import com.google.protobuf.Message;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

    <T extends Message> Event appendToStream(EventCandidate<T> candidate, String correlation);

    Stream<Event> readStream(String stream, String streamId);

    Stream<Event> readRawStream(String stream, String streamId);

    Statistics stat(String stream);

}