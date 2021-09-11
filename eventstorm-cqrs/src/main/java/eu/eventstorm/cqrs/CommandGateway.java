package eu.eventstorm.cqrs;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandGateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandGateway.class);

	private final ImmutableMap<String, CommandHandler<? extends Command,?>> handlers;

	private final ConcurrentMap<String, String> cache;

	CommandGateway(ImmutableMap<String, CommandHandler<? extends Command, ?>> handlers) {
		this.handlers = handlers;
		this.cache = new ConcurrentHashMap<>();
	}

	public <E> Flux<E> dispatch(CommandContext ctx) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dispatch({})", ctx);
		}

		String cacheKey = cache.get(ctx.getCommand().getClass().getName());

		if (cacheKey == null) {
			cacheKey = buildCacheKey(ctx.getCommand());
			this.cache.put(ctx.getCommand().getClass().getName(), cacheKey);
		}

		// if the command is not found -> command gateway exception -> no need to check if it's null.
		CommandHandler<Command,E> commandHandler = (CommandHandler<Command, E>) this.handlers.get(cacheKey);

		return commandHandler.handle(ctx);
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

		throw new CommandGatewayException(CommandGatewayException.Type.NOT_FOUND, of("command", command));
	}

	public static CommandGateway.Builder newBuilder() {
		return new CommandGateway.Builder();
	}

	public static class Builder {

		private final ImmutableMap.Builder<String, CommandHandler<? extends Command,?>> mapBuilder;

		private Builder() {
			this.mapBuilder = ImmutableMap.builder();
		}

		public <E> CommandGateway.Builder add(CommandHandler<? extends Command, E> commandHandler) {
			this.mapBuilder.put(commandHandler.getType().getName(), commandHandler);
			return this;
		}

		public CommandGateway build() {
			return new CommandGateway(mapBuilder.build());
		}

	}

}
