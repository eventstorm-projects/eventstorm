package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;


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
        validatorContext.forEach(v -> {
            v.buildMessage(builder);
            builder.append(", ");
        });
        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public String getCode() {
        return validatorContext.getCode();
    }
}
