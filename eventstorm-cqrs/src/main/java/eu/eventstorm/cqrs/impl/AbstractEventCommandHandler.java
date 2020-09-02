package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractEventCommandHandler<T extends Command> implements CommandHandler<T, Event> {
	
	
	public final ImmutableList<Event> handle(CommandContext context, T command) {
		
		// validate the command
		validate(context, command);
		
		// apply the decision function (state,command) => events
		ImmutableList<EventCandidate<?>> candidates = decision(context, command);
		
		// save the to the eventStore
		ImmutableList<Event> events = store(candidates);
		
		// apply the evolution function (state,Event) => State
		evolution(events);
		
		// publish events
		publish(events);
		
		return events;
	}

	
	protected abstract void validate(CommandContext context, T command);

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate<?>> decision(CommandContext context, T command);

	
	protected abstract ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates);

	/**
	 *  (state,Event) => State
	 */
	protected abstract void evolution(ImmutableList<Event> events);
	
	/**
	 * publish events
	 */
	protected abstract void publish(ImmutableList<Event> events);

}