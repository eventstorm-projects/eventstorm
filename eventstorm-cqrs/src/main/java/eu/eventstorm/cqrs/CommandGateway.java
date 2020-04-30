package eu.eventstorm.cqrs;

import static com.google.common.collect.ImmutableMap.of;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventbus.EventBus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.context.Context;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

    private final CommandHandlerRegistry registry;
    
    private final EventBus eventBus;
    
    private final Scheduler scheduler;

    public CommandGateway(Scheduler scheduler, CommandHandlerRegistry registry, EventBus eventBus) {
        this.registry = registry;
        this.eventBus = eventBus;
        this.scheduler = scheduler;
    }

	public <T extends Command> Flux<Event> dispatch(T command) {

		return Mono.just(command)
				// async
				.publishOn(scheduler)
				.subscriberContext(Context.of("command", command))
				// retrieve command handler associated
				.flatMap(c -> Mono.justOrEmpty(registry.get(c)))
				// if no command handler => error
				.switchIfEmpty(Mono.error(new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, of("command", command))))
				// handle the Command
				.flatMapMany(ch -> ch.handle(command))
				// publish events
				.doOnNext(this.eventBus::publish)
				;
    }

}
