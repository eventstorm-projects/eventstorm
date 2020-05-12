package eu.eventstorm.cqrs;

import static com.google.common.collect.ImmutableMap.of;

import eu.eventstorm.core.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

    private final CommandHandlerRegistry registry;
    
    private final Scheduler scheduler;

    public CommandGateway(Scheduler scheduler, CommandHandlerRegistry registry) {
        this.registry = registry;
        this.scheduler = scheduler;
    }

	public <T extends Command> Flux<Event> dispatch(T command) {

		return Mono.just(command)
				// retrieve command handler associated
				.flatMap(c -> Mono.justOrEmpty(registry.get(c)))
				// if no command handler => error
				.switchIfEmpty(Mono.error(new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, of("command", command))))
				// async
				.publishOn(scheduler)
				// handle the Command
				.map(ch -> ch.handle(command))
				// convert
				.flatMapMany(Flux::fromIterable)
				;
    }

}
