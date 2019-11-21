package eu.eventstorm.core.eventstore;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

@SuppressWarnings("serial")
public final class EventStoreException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		STREAM_NOT_FOUND
	}
	
	public EventStoreException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
