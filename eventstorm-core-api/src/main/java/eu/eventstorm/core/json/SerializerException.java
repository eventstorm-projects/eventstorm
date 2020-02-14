package eu.eventstorm.core.json;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SerializerException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		WRITE_ERROR
	}
	
	public SerializerException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
