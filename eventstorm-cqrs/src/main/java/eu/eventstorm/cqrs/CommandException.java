package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class CommandException extends RuntimeException {

    private final transient ImmutableMap<String, Object> parameters;
    private final transient Command command;
    
    protected CommandException(Command command, String message, ImmutableMap<String, Object> parameters) {
        super(message);
        this.parameters = parameters;
        this.command = command;
    }

    protected CommandException(Command command, String message, Throwable cause, ImmutableMap<String, Object> parameters) {
        super(message, cause);
        this.parameters = parameters;
        this.command = command;
    }

    public final ImmutableMap<String, Object> getParameters() {
        return parameters;
    }

	public final Command getCommand() {
		return command;
	}

}