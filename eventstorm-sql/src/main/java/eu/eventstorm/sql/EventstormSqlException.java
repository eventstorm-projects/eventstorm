package eu.eventstorm.sql;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public abstract class EventstormSqlException extends RuntimeException {

	private final transient EventstormSqlExceptionType type;
	private final transient ImmutableMap<String, Object> values;


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

	public final EventstormSqlExceptionType getType() {
		return type;
	}

	public final ImmutableMap<String, Object> getValues() {
		return values;
	}

	private static String build(EventstormSqlExceptionType type, ImmutableMap<String, Object> params, Throwable cause) {
		StringBuilder builder = new StringBuilder(256);
        builder.append("type=[").append(type).append("]");
        if (params != null) {
    		params.forEach((key, value) -> {
	    		builder.append(" [").append(key).append("]=[").append(value).append("]");
		    });
        }
		if (cause != null) {
			builder.append(" cause=[").append(cause.getMessage()).append("]");
		}
		return builder.toString();
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