package eu.eventstorm.batch.rest;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

public class ResourceException extends EventstormException{

	public enum Type implements EventstormExceptionType {
		CONVERT_ERROR
	}
	
	public ResourceException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(type, values, cause);
	}

}
