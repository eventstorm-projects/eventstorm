package eu.eventstorm.cqrs;

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
		// if the command is not found -> command gateway exception -> no need to check if it's null.
		return registry.<T, E>get(command).handle(ctx, command);
	}

}
