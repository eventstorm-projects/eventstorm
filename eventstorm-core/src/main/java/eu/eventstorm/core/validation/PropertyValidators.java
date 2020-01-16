package eu.eventstorm.core.validation;

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

	public static PropertyValidator<String> notEmpty() {
		return NOT_EMPTY;
	}
}
