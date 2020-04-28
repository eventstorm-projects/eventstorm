package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Validator<T> {

	ImmutableList<ConstraintViolation> validate(T object);

}
