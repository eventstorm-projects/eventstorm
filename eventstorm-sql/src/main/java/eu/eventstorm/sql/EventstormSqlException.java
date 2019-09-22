package eu.eventstorm.sql;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public class EventstormSqlException extends RuntimeException {

	private final EventstormSqlExceptionType type;
	private final ImmutableMap<String, Object> values;
	
	
	public EventstormSqlException(EventstormSqlExceptionType type, ImmutableMap<String, Object> values) {
		super(build(type, values, null));
		this.type = type;
		this.values = values;
	}
	
	public EventstormSqlException(EventstormSqlExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(build(type, values, cause), cause);
		this.type = type;
		this.values = values;
	}
	
	public EventstormSqlExceptionType getType() {
		return type;
	}

	public ImmutableMap<String, Object> getValues() {
		return values;
	}

	private static String build(EventstormSqlExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		return null;
	}

	@Deprecated
	public EventstormSqlException(String message) {
		this(null, null, null);
    }

	@Deprecated
    public EventstormSqlException(String message, Throwable cause) {
        this(null,null,null);
    }

}