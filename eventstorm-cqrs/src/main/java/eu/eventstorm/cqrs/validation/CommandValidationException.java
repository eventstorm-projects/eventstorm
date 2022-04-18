package eu.eventstorm.cqrs.validation;

import eu.eventstorm.core.validation.ValidationException;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandValidationException extends ValidationException {

    private final transient Command command;

    public CommandValidationException(CommandContext commandContext) {
        super(commandContext);
        this.command = commandContext.getCommand();
    }

    public Command getCommand() {
        return command;
    }

}
