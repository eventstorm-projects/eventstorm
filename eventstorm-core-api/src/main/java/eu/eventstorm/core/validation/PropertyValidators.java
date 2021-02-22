package eu.eventstorm.core.validation;

import java.util.List;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PropertyValidators {

	private PropertyValidators() {
	}

	private static final PropertyValidator<Object> NOT_NULL = (property, value, context) -> {
		if (value == null) {
			context.add(ConstraintViolations.ofNullProperty(property, "isNull"));
		}
	};
	
	private static final PropertyValidator<String> NOT_EMPTY = (property, value, context) -> {
		if (Strings.isEmpty(value)) {
			context.add(ConstraintViolations.ofNullProperty(property, "isEmpty"));
		}
	};
	
	private static final PropertyValidator<List<?>> LIST_NOT_EMPTY = (property, value, context) -> {
		if (value == null) {
			context.add(ConstraintViolations.ofNullProperty(property, "isNull"));
			return;
		}
		if (value.isEmpty()) {
			context.add(ConstraintViolations.ofNullProperty(property, "isEmpty"));
		}
	};
	
	public static PropertyValidator<Object> notNull() {
		return NOT_NULL;
	}

	public static PropertyValidator<String> notEmpty() {
		return NOT_EMPTY;
	}
	
	public static PropertyValidator<List<?>> listNotEmpty() {
		return LIST_NOT_EMPTY;
	}
}
