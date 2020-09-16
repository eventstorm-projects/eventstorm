package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;

@SuppressWarnings("serial")
public class ValidationException extends RuntimeException{

	private final transient ImmutableList<ConstraintViolation> constraintViolations;

	public ValidationException(ImmutableList<ConstraintViolation> constraintViolations) {
		super(build(constraintViolations));
		this.constraintViolations = constraintViolations;
	}

	public ImmutableList<ConstraintViolation> getConstraintViolations() {
		return constraintViolations;
	}

	private static String build(ImmutableList<ConstraintViolation> constraintViolations) {
		StringBuilder builder = new StringBuilder();
		builder.append("Validation Exception : ").append(constraintViolations.size());
		constraintViolations.forEach(v -> {
			builder.append("\n\t").append(v.getProperties()).append(" -> " ).append(v.getCause());
		});
		return builder.toString();
	}
}
