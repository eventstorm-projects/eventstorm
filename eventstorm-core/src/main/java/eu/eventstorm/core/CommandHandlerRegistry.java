package eu.eventstorm.core;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandHandlerRegistry {

	private final ImmutableMap<Class<? extends Command>, CommandHandler<? extends Command>> handlers;

	CommandHandlerRegistry(ImmutableMap<Class<? extends Command>, CommandHandler<? extends Command>> handlers) {
		this.handlers = handlers;
	}

	@SuppressWarnings("unchecked")
	public <T extends Command> CommandHandler<T> get(Class<T> command) {
		return (CommandHandler<T>) this.handlers.get(command);
	}

}