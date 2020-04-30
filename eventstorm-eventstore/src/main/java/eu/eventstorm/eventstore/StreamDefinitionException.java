package eu.eventstorm.eventstore;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class StreamDefinitionException extends EventstormException {

	public static final String STREAM = "stream";
	public static final String STREAM_EVENT_TYPE = "streamEventType";
	//public static final String STREAM_PAYLOAD = "streamPayload";

	public enum Type implements EventstormExceptionType {
		UNKNOW_STREAM, UNKNOW_STREAM_EVENT, INVALID_STREAM, NVALID_STREAM_EVENT_TYPE; //, INVALID_STREAM_PAYLOAD_CLASS, INVALID_STREAM_PAYLOAD_TYPE;
	}

	public StreamDefinitionException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

	public StreamDefinitionException(EventstormExceptionType type, ImmutableMap<String, Object> values, Exception cause) {
		super(type, values, cause);
	}

}
