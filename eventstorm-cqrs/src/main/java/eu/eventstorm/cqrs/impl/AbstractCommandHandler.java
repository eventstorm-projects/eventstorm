package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandHandler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractCommandHandler<T extends Command> implements CommandHandler<T> {
	
	
	public final ImmutableList<Event> handle(T command) {
		
		// validate the command
		validate(command);
		
		// apply the decision function (state,command) => events
		ImmutableList<EventCandidate<?>> candidates = decision(command);
		
		// save the to the eventStore
		ImmutableList<Event> events = store(candidates);
		
		// apply the evolution function (state,Event) => State
		evolution(events);
		
		// publish events
		publish(events);
		
		return events;
	}

	
	protected abstract void validate(T command);

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate<?>> decision(T command);

	
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