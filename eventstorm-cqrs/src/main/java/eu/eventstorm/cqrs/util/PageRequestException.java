package eu.eventstorm.cqrs.util;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class PageRequestException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		EMPTY, PARSING, INVALID_OP
	}
	
	public PageRequestException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}
	
	public PageRequestException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(type, values, cause);
	}

}