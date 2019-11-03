package eu.eventstorm.core;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandHandlerRegistryBuilder {

	private final ImmutableMap.Builder<Class<? extends Command>, CommandHandler<? extends Command>> builder;
	
	public CommandHandlerRegistryBuilder() {
		this.builder = ImmutableMap.builder();
	}

	public <T extends Command> CommandHandlerRegistryBuilder add(Class<T> command, CommandHandler<? super T> commandHandler) {
		this.builder.put(command, commandHandler);
		return this;
	}
	
	public CommandHandlerRegistry build() {
		return new CommandHandlerRegistry(builder.build());
	}
	
}
