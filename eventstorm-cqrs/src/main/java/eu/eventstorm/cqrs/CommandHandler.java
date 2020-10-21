package eu.eventstorm.cqrs;

import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command, T> {
	
	Class<C> getType();
	
	Flux<T> handle(CommandContext context, C command);

}