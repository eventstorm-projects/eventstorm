package eu.eventstorm.core.json;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class DeserializerException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		PARSE_ERROR, FIELD_NOT_FOUND
	}
	
	public DeserializerException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}
	
	public DeserializerException(EventstormExceptionType type, ImmutableMap<String, Object> values, Exception cause) {
		super(type, values, cause);
	}

}
