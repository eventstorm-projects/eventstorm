package eu.eventstorm.cqrs.context;

import eu.eventstorm.core.validation.ValidatorContextImpl;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class DefaultCommandContext extends ValidatorContextImpl implements CommandContext {

    private final Command command;

    public DefaultCommandContext(Command command) {
        this.command = command;
    }

    @Override
    public <T extends Command> T getCommand() {
        return (T) this.command;
    }

}
