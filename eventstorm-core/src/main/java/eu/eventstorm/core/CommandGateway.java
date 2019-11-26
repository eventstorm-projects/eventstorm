package eu.eventstorm.core;

import static com.google.common.collect.ImmutableMap.of;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class CommandGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGateway.class);

    private final CommandHandlerRegistry registry;
    
    private final EventBus eventBus;

    public CommandGateway(CommandHandlerRegistry registry, EventBus eventBus) {
        this.registry = registry;
        this.eventBus = eventBus;
    }

    @SuppressWarnings("unchecked")
	public <T extends Command> ImmutableList<Event<EventData>> dispatch(T command) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dispatch [{}]", command);
        }

        // 1. retrieve command handler
        CommandHandler<T> ch = (CommandHandler<T>) registry.get(command);

        if (ch == null) {
        	throw new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, of("command", command));
        }
        
        // 2. handler
        ImmutableList<Event<EventData>> events = ch.handle(command);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("events to publish [{}]", events);
        }
        // 3. publish event
        this.eventBus.publish(events);
        
        // 4. return events
        return events;
    }

}
