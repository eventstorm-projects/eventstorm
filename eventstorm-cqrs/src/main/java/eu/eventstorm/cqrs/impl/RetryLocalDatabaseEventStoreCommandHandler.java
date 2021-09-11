package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class RetryLocalDatabaseEventStoreCommandHandler<T extends Command> extends LocalDatabaseEventStoreCommandHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryLocalDatabaseEventStoreCommandHandler.class);

    private static final int DEFAULT_MAX_RETRY = 10;

    private final int maxRetry;

    protected RetryLocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator) {
        super(type, validator);
        this.maxRetry = DEFAULT_MAX_RETRY;
    }

    protected RetryLocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator, boolean publish) {
        super(type, validator, publish);
        this.maxRetry = DEFAULT_MAX_RETRY;
    }

    protected RetryLocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator, boolean publish, int maxRetry) {
        super(type, validator, publish);
        this.maxRetry = maxRetry;
    }

    @Override
    protected  Mono<ImmutableList<Event>> eventLoopStoreAndEvolution(CommandContext ctx) {
        return super.eventLoopStoreAndEvolution(ctx)
                .onErrorResume(LocalDatabaseStorageException.class, cause -> {
                    AtomicInteger counter = ctx.get(LocalDatabaseStorageException.class.getName());
                    if (counter == null) {
                        counter = new AtomicInteger(0);
                        ctx.put(LocalDatabaseStorageException.class.getName(), counter);
                    }
                    if (counter.incrementAndGet() > 10) {
                        LOGGER.warn("Max retry [{}] reached -> return last error", this.maxRetry);
                        return Mono.error(cause.getCause());
                    }
                    return this.eventLoopStoreAndEvolution(ctx);
                });
    }
}
