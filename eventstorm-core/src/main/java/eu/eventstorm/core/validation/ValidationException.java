package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Command;

@SuppressWarnings("serial")
public class ValidationException extends RuntimeException {

	public ValidationException(ImmutableList<ConstraintViolation> constraintViolations, Command command) {
	}

}
