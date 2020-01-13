package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Command;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Validator<T extends Command> {

	ImmutableList<ConstraintViolation> validate(T command);
	
	RuntimeException createNewException(ImmutableList<ConstraintViolation> violations, T command);
	
}
