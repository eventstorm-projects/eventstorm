package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventstormException;
import eu.eventstorm.core.EventstormExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class QueryException extends EventstormException {

	public enum Type implements EventstormExceptionType {
		FAILED_TO_WRITE_PAGE
	}

	public QueryException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(type, values);
	}

}
