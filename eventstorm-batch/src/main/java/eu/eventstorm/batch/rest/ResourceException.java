package eu.eventstorm.batch.rest;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class ResourceException extends EventstormException{

	public enum Type implements EventstormExceptionType {
		X_META_NOT_FOUND, X_META_FAILED_TO_READ
	}
	
	public ResourceException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(type, values, cause);
	}
	public ResourceException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
