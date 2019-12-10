package eu.eventstorm.core.eventstore;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class EventStoreException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		STREAM_NOT_FOUND, FAILED_TO_SERILIAZE_PAYLOAD
	}
	
	public EventStoreException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}
	
	public EventStoreException(EventstormExceptionType type, ImmutableMap<String, Object> values, Exception cause) {
		super(type, values, cause);
	}

}
