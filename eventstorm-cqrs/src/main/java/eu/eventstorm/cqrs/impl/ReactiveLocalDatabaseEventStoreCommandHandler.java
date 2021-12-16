package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.tracer.Span;
import eu.eventstorm.cqrs.tracer.Tracer;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.ReactiveValidator;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.util.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.util.UUID;

import static reactor.util.function.Tuples.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ReactiveLocalDatabaseEventStoreCommandHandler<T extends Command> implements CommandHandler<T, Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveLocalDatabaseEventStoreCommandHandler.class);

    private final Class<T> type;

    private final ReactiveValidator contractValidator;

    private final ReactiveValidator consistencyValidator;

    @Autowired
    private LocalDatabaseEventStore eventStore;

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

    protected ReactiveLocalDatabaseEventStoreCommandHandler(Class<T> type, ReactiveValidator contractValidator, ReactiveValidator consistencyValidator) {
        this(type, contractValidator, consistencyValidator, true);
    }

    protected ReactiveLocalDatabaseEventStoreCommandHandler(Class<T> type, ReactiveValidator contractValidator, ReactiveValidator consistencyValidator, boolean publish) {
        this.type = type;
        this.contractValidator = contractValidator;
        this.consistencyValidator = consistencyValidator;
        this.publish = publish;
    }

    @Override
    public final Class<T> getType() {
        return this.type;
    }


    public final Flux<Event> handle(CommandContext context) {
        return this.validate(context)
                .flatMap(this::init)
                .flatMap(this::eventLoopStoreAndEvolution)
                .flatMap(this::post)
                .flatMap(this::publish)
                .flatMapMany(r -> Flux.fromIterable(r.getT2()));
    }

    protected Mono<CommandContext> init(CommandContext ctx) {
        return Mono.just(ctx);
    }

    protected Mono<Tuple2<CommandContext, ImmutableList<Event>>> post(Tuple2<CommandContext, ImmutableList<Event>> tuple) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("post");
        }
        return Mono.just(tuple);
    }

    private Mono<Tuple2<CommandContext, ImmutableList<Event>>> publish(Tuple2<CommandContext, ImmutableList<Event>> tuple) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("publish ? [{}] : [{}]", publish, tuple);
        }
        if (publish) {
            eventLoop.post().schedule(() -> eventBus.publish(tuple.getT2()));
        }
        return Mono.just(tuple);
    }

    private Mono<Tuple2<CommandContext, ImmutableList<Event>>> eventLoopStoreAndEvolution(CommandContext ctx) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("eventLoopStoreAndEvolution [{}]", ctx);
        }
        return Mono.just(ctx)
                .publishOn(eventLoop.get(ctx.getCommand()))
                .handle(this::storeAndEvolution)
                .publishOn(eventLoop.post())
                ;
    }


    private void storeAndEvolution(CommandContext ctx, SynchronousSink<Tuple2<CommandContext, ImmutableList<Event>>> sink) {
        try (Span ignored = this.tracer.start("storeAndEvolution")) {
            sink.next(of(ctx, doStoreAndEvolution(ctx)));
        } catch (Exception cause) {
            sink.error(cause);
        }
    }

    private ImmutableList<Event> doStoreAndEvolution(CommandContext ctx) {
        return transactionTemplate.executeWithReadWrite(() -> {
            ImmutableList<Event> events;
            ImmutableList<EventCandidate<?>> candidates;
            try (Span ignored = this.tracer.start("decision")) {
                // apply the decision function (state,command) => events
                candidates = decision(ctx);
            }

            try (Span ignored = this.tracer.start("store")) {
                // save to the eventStore
                events = store(candidates, ctx.getCorrelation());
            }

            try (Span ignored = this.tracer.start("evolution")) {
                // apply the evolution function (state,Event) => State
                events.forEach(evolutionHandlers::on);
            }
            return events;
        });
    }

    private Mono<CommandContext> validate(CommandContext ctx) {
        return contractValidator.validate(ctx)
                .flatMap(result -> {
                    if (result.hasConstraintViolation()) {
                        return Mono.error(new CommandValidationException(result));
                    }
                    return this.consistencyValidator.validate(result)
                            .flatMap(consistencyResult -> {
                                if (consistencyResult.hasConstraintViolation()) {
                                    return Mono.error(new CommandValidationException(consistencyResult));
                                }
                                return Mono.just(consistencyResult);
                            });
                });

    }

    /**
     * (state,command) => events
     */
    protected abstract ImmutableList<EventCandidate<?>> decision(CommandContext context);

    private ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates, String correlation) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("store [{}]", candidates);
        }
        ImmutableList.Builder<Event> builder = ImmutableList.builder();
        if (correlation == null && candidates.size() > 1) {
            correlation = UUID.randomUUID().toString();
        }
        try {
            for (EventCandidate<?> candidate : candidates) {
                builder.add(this.eventStore.appendToStream(candidate, correlation));
            }
        } catch (EventstormRepositoryException cause) {
            throw new LocalDatabaseEventStoreCommandHandler.LocalDatabaseStorageException(cause);
        }
        return builder.build();
    }
}