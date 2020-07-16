package eu.eventstorm.cqrs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandHandlerRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerRegistry.class);

	private final ImmutableMap<String, CommandHandler<? extends Command, ?>> handlers;

	private final ConcurrentMap<String, String> cache;

	CommandHandlerRegistry(ImmutableMap<String, CommandHandler<? extends Command, ?>> handlers) {
		this.handlers = handlers;
		this.cache = new ConcurrentHashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T extends Command, E> CommandHandler<T,E> get(Command key) {

		String cacheKey = cache.get(key.getClass().getName());

		if (cacheKey == null) {
			cacheKey = buildCacheKey(key);
			this.cache.put(key.getClass().getName(), cacheKey);
		}

		return (CommandHandler<T,E>) this.handlers.get(cacheKey);
	}

	private String buildCacheKey(Command command) {
	    
	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("buildCacheKey({})", command);
	    }

		Class<? extends Command> c = command.getClass();

		String key = command.getClass().getName();

		if (this.handlers.containsKey(key)) {
			return key;
		}

		 if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("buildCacheKey() -> check interfaces");
	        }
		 
		for (Class<?> item : c.getInterfaces()) {
		    if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("buildCacheKey interface : [{}]", item);
	        }
			if (this.handlers.containsKey(item.getName())) {
				return item.getName();
			}
		}

		return null;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private final ImmutableMap.Builder<String, CommandHandler<? extends Command, ?>> mapBuilder;

		private Builder() {
			this.mapBuilder = ImmutableMap.builder();
		}

		public <T extends Command, E> Builder add(CommandHandler<T, E> commandHandler) {
			this.mapBuilder.put(commandHandler.getType().getName(), commandHandler);
			return this;
		}

		public CommandHandlerRegistry build() {
			return new CommandHandlerRegistry(mapBuilder.build());
		}

	}

}