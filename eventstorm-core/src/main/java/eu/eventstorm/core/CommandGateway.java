package eu.eventstorm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class CommandGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGateway.class);

   //private final CommandHandler<CreateUserCommand> commandHandler;
    private final EventBus eventBus;

    private final CommandHandlerRegistry registry;

    public CommandGateway(CommandHandlerRegistry registry, EventBus eventBus) {
     //   this.commandHandler = commandHandler;
        this.registry = registry;
        this.eventBus = eventBus;
     //   this.registry = ServiceLoader.load(CommandHandlerRegistry.class).iterator().next();
    }

    @SuppressWarnings("unchecked")
	public <T extends Command> void dispatch(T command) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dispatch [{}]", command);
        }


        // 1. retrieve command handler
        CommandHandler<T> ch = (CommandHandler<T>) registry.get(command.getClass());

        // 2. handler
        Event event = ch.handle(command);

        // 3. dispatch
        eventBus.publish(event);


    }

}
