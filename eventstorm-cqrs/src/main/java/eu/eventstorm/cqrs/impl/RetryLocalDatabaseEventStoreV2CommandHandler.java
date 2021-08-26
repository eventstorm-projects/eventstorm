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
public abstract class RetryLocalDatabaseEventStoreV2CommandHandler<T extends Command> extends LocalDatabaseEventStoreV2CommandHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryLocalDatabaseEventStoreV2CommandHandler.class);

    private static final int DEFAULT_MAX_RETRY = 10;

    private final int maxRetry;

    protected RetryLocalDatabaseEventStoreV2CommandHandler(Class<T> type, Validator<T> validator, Validator<T> consistencyValidator) {
        super(type, validator, consistencyValidator);
        this.maxRetry = DEFAULT_MAX_RETRY;
    }

    protected RetryLocalDatabaseEventStoreV2CommandHandler(Class<T> type, Validator<T> validator, Validator<T> consistencyValidator, boolean publish) {
        super(type, validator, consistencyValidator, publish);
        this.maxRetry = DEFAULT_MAX_RETRY;
    }

    protected RetryLocalDatabaseEventStoreV2CommandHandler(Class<T> type, Validator<T> validator, Validator<T> consistencyValidator, boolean publish, int maxRetry) {
        super(type, validator, consistencyValidator, publish);
        this.maxRetry = maxRetry;
    }

    @Override
    protected  Mono<ImmutableList<Event>> eventLoopStoreAndEvolution(Tuple2<CommandContext, T> tp) {
        return super.eventLoopStoreAndEvolution(tp)
                .onErrorResume(LocalDatabaseStorageException.class, cause -> {
                    AtomicInteger counter = tp.getT1().get(LocalDatabaseStorageException.class.getName());
                    if (counter == null) {
                        counter = new AtomicInteger(0);
                        tp.getT1().put(LocalDatabaseStorageException.class.getName(), counter);
                    }
                    if (counter.incrementAndGet() > 10) {
                        LOGGER.warn("Max retry [{}] reached -> return last error", this.maxRetry);
                        return Mono.error(cause.getCause());
                    }
                    return this.eventLoopStoreAndEvolution(tp);
                });
    }
}
