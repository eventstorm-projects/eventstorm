package eu.eventstorm.eventstore;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class StreamDefinitionException extends EventstormException {

	public static final String STREAM = "stream";
	public static final String STREAM_EVENT_TYPE = "streamEventType";

	public enum Type implements EventstormExceptionType {
		UNKNOWN_STREAM, UNKNOWN_STREAM_EVENT
	}

	private StreamDefinitionException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

	public static StreamDefinitionException newUnknownStream(String stream) {
		return new StreamDefinitionException(StreamDefinitionException.Type.UNKNOWN_STREAM, of(STREAM, stream));
	}

	public static StreamDefinitionException newUnknownStreamType(String stream, String eventType) {
		return new StreamDefinitionException(StreamDefinitionException.Type.UNKNOWN_STREAM_EVENT, of(STREAM, stream, STREAM_EVENT_TYPE, eventType));
	}

}