package eu.eventstorm.cqrs;

import static com.google.common.collect.ImmutableMap.of;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

    private final CommandHandlerRegistry registry;
    
    public CommandGateway(CommandHandlerRegistry registry) {
        this.registry = registry;
    }

	public <T extends Command, E> Flux<E> dispatch(CommandContext ctx, T cmd) {

		return Mono.just(Tuples.of(ctx, cmd))
				// retrieve command handler associated
				.flatMapMany(tuple -> {
					CommandHandler<T,E> handler = registry.<T,E>get(tuple.getT2());
					// if no command handler => error
					if (handler == null) {
						throw new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, of("command", tuple.getT2()));
					}
					return handler.handle(tuple.getT1(), tuple.getT2());
				});
    }

}
