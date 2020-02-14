package eu.eventstorm.cqrs;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command> {
	
	Class<C> getType();
	
	Flux<Event<EventPayload>> handle(C command);

}