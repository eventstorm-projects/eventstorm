package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class CommandGatewayException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		NOT_FOUND, NO_HANDLER_IN_REGISTRY
	}

	public CommandGatewayException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
