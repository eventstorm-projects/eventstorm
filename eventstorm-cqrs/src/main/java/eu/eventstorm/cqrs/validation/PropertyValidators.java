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

	private static final PropertyValidator<String> NOT_EMPTY = (properties, value, builder) -> {
		if (Strings.isEmpty(value)) {
			builder.add(new ConstraintViolationImpl(properties, "isEmpty"));
		}
	};
	
	private static final PropertyValidator<List<?>> LIST_NOT_EMPTY = (properties, value, builder) -> {
		if (value == null) {
			builder.add(new ConstraintViolationImpl(properties, "isNull"));
			return;
		}
		if (value.isEmpty()) {
			builder.add(new ConstraintViolationImpl(properties, "isEmpty"));
		}
	};

	public static PropertyValidator<String> notEmpty() {
		return NOT_EMPTY;
	}
	
	public static PropertyValidator<List<?>> listNotEmpty() {
		return LIST_NOT_EMPTY;
	}
}
