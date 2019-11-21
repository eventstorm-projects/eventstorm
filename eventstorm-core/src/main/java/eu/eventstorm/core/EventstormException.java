package eu.eventstorm.core;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public abstract class EventstormException extends RuntimeException {

	private final EventstormExceptionType type;
	private final ImmutableMap<String, Object> values;


	public EventstormException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super(build(type, values, null));
		this.type = type;
		this.values = values;
	}

	public EventstormException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(build(type, values, cause), cause);
		this.type = type;
		this.values = values;
	}

	public EventstormExceptionType getType() {
		return type;
	}

	public ImmutableMap<String, Object> getValues() {
		return values;
	}

	private static String build(EventstormExceptionType type, ImmutableMap<String, Object> params, Throwable cause) {
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

}