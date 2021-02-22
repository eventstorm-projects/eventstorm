package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.ValidatorContext;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class ValidationException extends RuntimeException {

	private final transient ValidatorContext validatorContext;

	public ValidationException(ValidatorContext validatorContext) {
		super(build(validatorContext));
		this.validatorContext = validatorContext;
	}

	public ImmutableList<ConstraintViolation> getConstraintViolations() {
		ImmutableList.Builder<ConstraintViolation> builder = ImmutableList.builder();
		validatorContext.forEach(builder::add);
		return builder.build();
	}

	private static String build(ValidatorContext validatorContext) {
		StringBuilder builder = new StringBuilder();
		//builder.append(" : ").append(constraintViolations.size());
		validatorContext.forEach(v -> {
			builder.append("\n\t");
			v.buildMessage(builder);
		});
		return builder.toString();
	}

	public String getCode() {
		return validatorContext.getCode();
	}
}
