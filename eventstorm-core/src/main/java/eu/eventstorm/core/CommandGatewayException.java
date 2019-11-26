package eu.eventstorm.core;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class CommandGatewayException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		NOT_FOUND
	}

	public CommandGatewayException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
