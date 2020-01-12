package eu.eventstorm.core.validation;

import java.util.function.Predicate;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PropertyValidators {

	private PropertyValidators() {
	}

	private static final Predicate<String> IS_EMPTY = value -> Strings.isEmpty(value);
	
	public static Predicate<String> isEmpty() {
		return IS_EMPTY;
	}
}
