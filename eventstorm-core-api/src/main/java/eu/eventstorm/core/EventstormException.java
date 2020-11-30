package eu.eventstorm.core;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public abstract class EventstormException extends RuntimeException {

	private final transient EventstormExceptionType type;
	private final transient ImmutableMap<String, Object> values;

	public EventstormException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
		super();
		this.type = type;
		this.values = values;
	}

	public EventstormException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
		super(cause);
		this.type = type;
		this.values = values;
	}

	public final EventstormExceptionType getType() {
		return type;
	}

	public final ImmutableMap<String, Object> getValues() {
		return values;
	}

	
	@Override
	public String getMessage() {
		return build(type, this.values, getCause());
	}

	private static String build(EventstormExceptionType type, ImmutableMap<String, Object> params, Throwable cause) {
		StringBuilder builder = new StringBuilder(256);
		builder.append("type=[").append(type).append("]");
		if (params != null && params.size() > 0) {
			builder.append(", params:{");
			params.forEach((key, value) -> builder.append("[").append(key).append("]=[")
					.append(String.valueOf(value)).append("] "));
			builder.deleteCharAt(builder.length() -1);
			builder.append("}");
		}
		if (cause != null) {
			builder.append(" cause=[").append(cause.getMessage()).append("]");
		}
		return builder.toString();
	}

}