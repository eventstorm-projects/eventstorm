package eu.eventstorm.cqrs;

import static com.google.common.collect.ImmutableMap.of;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

    private final CommandHandlerRegistry registry;
    
    public CommandGateway(CommandHandlerRegistry registry) {
        this.registry = registry;
    }

	public <T extends Command, E> Flux<E> dispatch(CommandContext context, T command) {

		return Mono.just(command)
				// retrieve command handler associated
				.flatMap(c -> Mono.justOrEmpty(registry.<T,E>get(c)))
				// if no command handler => error
				.switchIfEmpty(Mono.error(() -> new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, of("command", command))))
				// handle the Command
				.flatMapMany(ch -> ch.handle(context, command))
				;
    }

}
