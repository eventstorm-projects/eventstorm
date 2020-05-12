package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command> {
	
	Class<C> getType();
	
	ImmutableList<Event> handle(C command);

}