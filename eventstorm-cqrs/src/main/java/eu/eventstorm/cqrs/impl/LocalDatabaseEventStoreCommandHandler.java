package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.tracer.Span;
import eu.eventstorm.cqrs.tracer.Tracer;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.db.AbstractLocalDatabaseEventStore;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.impl.TransactionDefinitions;
import eu.eventstorm.sql.util.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static reactor.util.function.Tuples.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class LocalDatabaseEventStoreCommandHandler<T extends Command> implements CommandHandler<T, Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDatabaseEventStoreCommandHandler.class);

    private static final Consumer<SignalType> DEFAULT_SIGNAL_TYPE_CONSUMER = signalType -> {
    };

    private final Class<T> type;

    private final Validator<T> validator;

    @Autowired
    private AbstractLocalDatabaseEventStore eventStore;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EvolutionHandlers evolutionHandlers;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private EventLoop eventLoop;

    @Autowired
    private Tracer tracer;

    private final boolean publish;

    protected LocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator) {
        this(type, validator, true);
    }

    protected LocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator, boolean publish) {
        this.type = type;
        this.validator = validator;
        this.publish = publish;
    }

    @Override
    public final Class<T> getType() {
        return this.type;
    }

    public final Flux<Event> handle(CommandContext context) {
        return Mono.just(context)
                .publishOn(eventLoop.validation())
                .handle(this::validate)
                .publishOn(eventLoop.get(context.getCommand()))
                .handle(this::storeAndEvolution)
                .flatMap(tuple -> Mono.just(tuple)
                .publishOn(eventLoop.post())
                .handle(this::postEventStore))
                .flatMapMany(Flux::fromIterable);

    }

    protected  Mono<ImmutableList<Event>> eventLoopStoreAndEvolution(CommandContext ctx) {
        return Mono.just(ctx)
                .publishOn(eventLoop.get(ctx.getCommand()))
                .handle(this::storeAndEvolution)
                .flatMap(tuple -> Mono.just(tuple)
                        .publishOn(eventLoop.post())
                        .handle(this::postEventStore))
                ;
    }

    private void postEventStore(Tuple2<CommandContext, ImmutableList<Event>> events, SynchronousSink<ImmutableList<Event>> sink) {

        BiConsumer<CommandContext, ImmutableList<Event>> consumer = doPostStoreAndEvolution();
        if (consumer != null) {
            eventLoop.post().schedule(() -> {
                try (Span ignored = this.tracer.start("postStoreAndEvolution")) {
                    consumer.accept(events.getT1(), events.getT2());
                }
            });
        }

        if (publish) {
            eventLoop.post().schedule(() -> publish(events.getT2()));
        }

        sink.next(events.getT2());
    }


    private void validate(CommandContext ctx, SynchronousSink<CommandContext> sink) {
        try (Span ignored = this.tracer.start("validate")) {
            this.validator.validate(ctx, ctx.getCommand());
            if (ctx.hasConstraintViolation()) {
                sink.error(new CommandValidationException(ctx));
                return;
            }

            transactionTemplate.executeWithReadOnly(() -> consistencyValidation(ctx, ctx.getCommand()));
            if (ctx.hasConstraintViolation()) {
                sink.error(new CommandValidationException(ctx));
                return;
            }

        } catch (Exception exception) {
            sink.error(exception);
            return;
        }

        try {
            postValidate(ctx, ctx.getCommand());
            sink.next(ctx);
        } catch (Exception cause) {
            sink.error(cause);
        }

    }

    private void storeAndEvolution(CommandContext ctx, SynchronousSink<Tuple2<CommandContext, ImmutableList<Event>>> sink) {
        ImmutableList<Event> events;
        try (Span ignored = this.tracer.start("storeAndEvolution")) {
            events = doStoreAndEvolution(ctx);
        } catch (Exception cause) {
            sink.error(cause);
            return;
        }
        sink.next(of(ctx, events));
    }

    private ImmutableList<Event> doStoreAndEvolution(CommandContext ctx) {
        return transactionTemplate.executeWithReadWrite(() -> {
            ImmutableList<Event> events;
            ImmutableList<EventCandidate<?>> candidates;
            try (Span ignored = this.tracer.start("decision")) {
                // apply the decision function (state,command) => events
                candidates = decision(ctx, ctx.getCommand());
            }

            try (Span ignored = this.tracer.start("store")) {
                // save the to the eventStore
                events = store(candidates, ctx.getCorrelation());
            }

            try (Span ignored = this.tracer.start("evolution")) {
                // apply the evolution function (state,Event) => State
                events.forEach(evolutionHandlers::on);
            }
            return events;
        });
    }

    private ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates, String correlation) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("store [{}]", candidates);
        }
        ImmutableList.Builder<Event> builder = ImmutableList.builder();
        try {
            candidates.forEach(candidate -> builder.add(this.eventStore.appendToStream(candidate,correlation)));
        } catch (EventstormRepositoryException cause) {
            throw new LocalDatabaseStorageException(cause);
        }
        return builder.build();
    }

    private void publish(ImmutableList<Event> events) {
        try (Span ignored = this.tracer.start("publish")) {
            eventBus.publish(events);
        }
    }

    protected void consistencyValidation(CommandContext context, T command) {
    }

    protected void postValidate(CommandContext commandContext, T command) {

    }

    /**
     * (state,command) => events
     */
    protected abstract ImmutableList<EventCandidate<?>> decision(CommandContext context, T command);

    protected BiConsumer<CommandContext, ImmutableList<Event>> doPostStoreAndEvolution() {
        return null;
    }

    protected Consumer<SignalType> onFinally(CommandContext context, T command) {
        return DEFAULT_SIGNAL_TYPE_CONSUMER;
    }


    static class LocalDatabaseStorageException extends RuntimeException {

        public LocalDatabaseStorageException(EventstormRepositoryException cause) {
            super(cause);
        }
    }

}