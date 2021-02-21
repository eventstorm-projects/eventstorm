package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.validation.ConstraintViolation;

import static java.util.stream.Collectors.joining;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
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
			builder.append("\n\t");
			v.buildMessage(builder);
		});
		return builder.toString();
	}

	public String getCode() {
		return constraintViolations.stream().map(ConstraintViolation::getCode).collect(joining(","));
	}
}
