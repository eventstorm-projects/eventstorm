package eu.eventstorm.core;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command> {

	ImmutableList<Event<EventData>> handle(C command);

}