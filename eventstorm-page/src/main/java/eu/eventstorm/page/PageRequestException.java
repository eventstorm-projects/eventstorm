package eu.eventstorm.page;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequestException extends RuntimeException {

	public enum Type  {
		EMPTY, PARSING, INVALID_OP, FILTER_ALREADY_SET
	}

	private final Type type;
	private final ImmutableMap<String, Object> values;

	public PageRequestException(Type type, ImmutableMap<String, Object> values) {
		super(build(type, values, null));
		this.type = type;
		this.values = values;
		//super(type, values);
	}

	public PageRequestException(Type type, ImmutableMap<String, Object> values, Throwable cause) {
		super(build(type, values, cause));
		this.type = type;
		this.values = values;
		//super(type, values, cause);
	}

	public ImmutableMap<String, Object> getValues() {
		return this.values;
	}

	public Type getType() {
		return this.type;
	}

	private static String build(Type type, ImmutableMap<String, Object> params, Throwable cause) {
		StringBuilder builder = new StringBuilder(256);
		builder.append("type=[").append(type).append("]");
		if (params != null && !params.isEmpty()) {
			builder.append(", params:{");
			params.forEach((key, value) -> builder.append("[").append(key).append("]=[")
					.append(value).append("] "));
			builder.deleteCharAt(builder.length() -1);
			builder.append("}");
		}
		if (cause != null) {
			builder.append(" cause=[").append(cause.getMessage()).append("]");
		}
		return builder.toString();
	}
}