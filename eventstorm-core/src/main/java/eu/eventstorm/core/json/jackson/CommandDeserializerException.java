package eu.eventstorm.core.json.jackson;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class CommandDeserializerException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		PARSE_ERROR, FIELD_NOT_FOUND
	}
	
	public CommandDeserializerException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
