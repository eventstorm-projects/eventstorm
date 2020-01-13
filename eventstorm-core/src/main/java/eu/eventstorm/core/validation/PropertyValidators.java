package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PropertyValidators {

	private PropertyValidators() {
	}

	private static final PropertyValidator<String> IS_EMPTY = (property, value, builder) -> {
		if (Strings.isEmpty(value)) {
			builder.add(new ConstraintViolationImpl(ImmutableList.of(property), "isEmpty"));
		}
	};

	public static PropertyValidator<String> isEmpty() {
		return IS_EMPTY;
	}
}
