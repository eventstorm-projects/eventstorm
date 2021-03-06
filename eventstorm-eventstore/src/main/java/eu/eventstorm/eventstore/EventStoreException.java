package eu.eventstorm.eventstore;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventStoreException extends EventstormException {

	public static final String PARAM_STREAM = "stream";
	
	public enum Type implements EventstormExceptionType {
		STREAM_NOT_FOUND, STREAM_EVENT_PAYLOAD_NOT_FOUND, FAILED_TO_SERIALIZE, FAILED_TO_DESERIALIZE
	}
	
	public EventStoreException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}
	
	public EventStoreException(EventstormExceptionType type, ImmutableMap<String, Object> values, Exception cause) {
		super(type, values, cause);
	}

}
