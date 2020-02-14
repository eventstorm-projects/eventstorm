package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.Command;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Validator<T extends Command> {

	ImmutableList<ConstraintViolation> validate(T command);

}
