package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;

import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

    private final CommandHandlerRegistry registry;
    
    public CommandGateway(CommandHandlerRegistry registry) {
        this.registry = registry;
    }

	public <T extends Command, E> Flux<E> dispatch(CommandContext ctx, T command) {
		CommandHandler<T,E> handler = registry.<T,E>get(command);
		// if no command handler => error
		if (handler == null) {
			throw new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, ImmutableMap.of("command", command));
		}
		return handler.handle(ctx, command);
    }

}
