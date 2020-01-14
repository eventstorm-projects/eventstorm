package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Command;

@SuppressWarnings("serial")
public final class CommandValidationException extends ValidationException {

	private final Command command;

	public CommandValidationException(ImmutableList<ConstraintViolation> constraintViolations, Command command) {
		super(constraintViolations);
		this.command = command;
	}

    public Command getCommand() {
        return command;
    }

}
