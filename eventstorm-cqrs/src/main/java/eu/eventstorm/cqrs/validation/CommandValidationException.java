package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.Command;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandValidationException extends ValidationException {

	private final transient Command command;

	public CommandValidationException(ImmutableList<ConstraintViolation> constraintViolations, Command command) {
		super(constraintViolations);
		this.command = command;
	}

    public Command getCommand() {
        return command;
    }

}
