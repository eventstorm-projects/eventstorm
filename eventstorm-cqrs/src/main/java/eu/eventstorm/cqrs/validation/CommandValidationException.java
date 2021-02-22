package eu.eventstorm.cqrs.validation;

import eu.eventstorm.core.validation.ValidationException;
import eu.eventstorm.core.validation.ValidatorContext;
import eu.eventstorm.cqrs.Command;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandValidationException extends ValidationException {

	private final transient Command command;

	public CommandValidationException(ValidatorContext validatorContext, Command command) {
		super(validatorContext);
		this.command = command;
	}

    public Command getCommand() {
        return command;
    }

}
