package eu.eventstorm.sql.dialect;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class EventstormDialectException extends EventstormSqlException {

	public enum Type implements EventstormSqlExceptionType {
		MODULE_NOT_FOUND, FAILED_TO_WRITE_JSON
	}

	public EventstormDialectException(Type type, ImmutableMap<String, Object> params) {
		super(type, params);
	}
	
	public EventstormDialectException(Type type, ImmutableMap<String, Object> params, Throwable cause) {
		super(type, params, cause);
	}

}
