package eu.eventstorm.cqrs.validation;

import java.util.List;

import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PropertyValidators {

	private PropertyValidators() {
	}

	private static final PropertyValidator<Object> NOT_NULL = (property, value, builder) -> {
		if (value == null) {
			builder.add(ConstraintViolations.ofNullProperty(property, "isNull"));
		}
	};
	
	private static final PropertyValidator<String> NOT_EMPTY = (property, value, builder) -> {
		if (Strings.isEmpty(value)) {
			builder.add(ConstraintViolations.ofNullProperty(property, "isEmpty"));
		}
	};
	
	private static final PropertyValidator<List<?>> LIST_NOT_EMPTY = (property, value, builder) -> {
		if (value == null) {
			builder.add(ConstraintViolations.ofNullProperty(property, "isNull"));
			return;
		}
		if (value.isEmpty()) {
			builder.add(ConstraintViolations.ofNullProperty(property, "isEmpty"));
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
