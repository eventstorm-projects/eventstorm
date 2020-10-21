package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractCommandHandler<T extends Command, R> implements CommandHandler<T, R> {
	
	public final Flux<R> handle(CommandContext context, T command) {
		// validate the command
		validate(context, command);
		
		// apply the decision function (state,command) => events
		ImmutableList<R> candidates = decision(context, command);
		
		// apply the evolution function (state,Event) => State
		evolution(candidates);
		
		return Flux.fromIterable(candidates);
	}

	
	protected abstract void validate(CommandContext context, T command);

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<R> decision(CommandContext context, T command);

	/**
	 *  (state,Event) => State
	 */
	protected abstract void evolution(ImmutableList<R> events);
	
}