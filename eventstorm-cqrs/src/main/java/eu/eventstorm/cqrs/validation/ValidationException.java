package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;

@SuppressWarnings("serial")
public class ValidationException extends RuntimeException{

	private final transient ImmutableList<ConstraintViolation> constraintViolations;

	public ValidationException(ImmutableList<ConstraintViolation> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

	public ImmutableList<ConstraintViolation> getConstraintViolations() {
		return constraintViolations;
	}
	
}
