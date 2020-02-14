package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public abstract class CommandException extends RuntimeException {

    private final transient ImmutableMap<String, Object> parameters;
    
    protected CommandException(String message, ImmutableMap<String, Object> parameters) {
        super(message);
        this.parameters = parameters;
    }

    protected CommandException(String message, Throwable cause, ImmutableMap<String, Object> parameters) {
        super(message, cause);
        this.parameters = parameters;
    }

    public ImmutableMap<String, Object> getParameters() {
        return parameters;
    }

}