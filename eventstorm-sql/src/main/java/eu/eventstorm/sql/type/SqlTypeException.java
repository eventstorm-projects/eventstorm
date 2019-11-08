package eu.eventstorm.sql.type;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SqlTypeException extends EventstormSqlException {

	public enum Type implements EventstormSqlExceptionType {
		WRITE_JSON, READ_JSON, AS_MAP_INVALID, AS_LIST_INVALID
	}

	public static final String PARAM_ADAPTEE = "adaptee";
	public static final String PARAM_CONTENT = "content";
	public static final String PARAM_CONTENT_OBJECT = "content_object";
	
	public SqlTypeException(Type type, ImmutableMap<String, Object> params, Exception cause) {
		super(type, params, cause);
	}

	public SqlTypeException(Type type, ImmutableMap<String, Object> params) {
		super(type, params);
	}
}
