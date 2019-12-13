package eu.eventstorm.core.json.jackson;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class CloudEventDeserializerException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		PARSE_ERROR
	}
	
	public CloudEventDeserializerException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(type, values, cause);
	}

}
