package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CommandHandler<C extends Command, T> {
	
	Class<C> getType();
	
	ImmutableList<T> handle(CommandContext context, C command);

}