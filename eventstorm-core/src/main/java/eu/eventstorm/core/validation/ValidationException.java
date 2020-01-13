package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class ValidationException extends RuntimeException{

	private final ImmutableList<ConstraintViolation> constraintViolations;

	public ValidationException(ImmutableList<ConstraintViolation> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

	public ImmutableList<ConstraintViolation> getConstraintViolations() {
		return constraintViolations;
	}
	
}
