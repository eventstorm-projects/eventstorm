package eu.eventstorm.core;

import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command> {
	
	Class<C> getType();
	
	Flux<Event<EventPayload>> handle(C command);

}