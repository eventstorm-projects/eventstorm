package eu.eventstorm.core;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command> {
	
	Class<C> getType();
	
	ImmutableList<Event<EventPayload>> handle(C command);

}