package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandHandler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractCommandHandler<T extends Command, R> implements CommandHandler<T, R> {
	
	public final ImmutableList<R> handle(T command) {
		
		// validate the command
		validate(command);
		
		// apply the decision function (state,command) => events
		ImmutableList<R> candidates = decision(command);
		
		// apply the evolution function (state,Event) => State
		evolution(candidates);
		
		return candidates;
	}

	
	protected abstract void validate(T command);

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<R> decision(T command);

	/**
	 *  (state,Event) => State
	 */
	protected abstract void evolution(ImmutableList<R> events);
	
}