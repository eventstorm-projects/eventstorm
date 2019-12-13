package eu.eventstorm.core.impl;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class EventPayloadRegistryBuilderException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		NOT_INTERFACE, MISSING_ANNOTATION_CQRS_EVENTPAYLOAD, DUPLICATE_TYPE
	}
	
	public EventPayloadRegistryBuilderException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
