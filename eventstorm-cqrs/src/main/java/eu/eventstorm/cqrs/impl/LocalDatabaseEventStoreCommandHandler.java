package eu.eventstorm.cqrs.impl;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import eu.eventstorm.cqrs.tracer.Span;
import eu.eventstorm.cqrs.tracer.Tracer;
import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.impl.TransactionDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import static reactor.util.function.Tuples.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class LocalDatabaseEventStoreCommandHandler<T extends Command> implements CommandHandler<T, Event> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDatabaseEventStoreCommandHandler.class);

	private static final TransactionDefinition DEFAULT_TRANSACTION_DEFINITION = TransactionDefinitions.readWrite(10);

	private static final Consumer<SignalType> DEFAULT_SIGNAL_TYPE_CONSUMER = signalType -> {
	};

	private final Class<T> type;

	private final Validator<T> validator;
	
	@Autowired
	private LocalDatabaseEventStore eventStore;
	
	@Autowired
	private TransactionManager transactionManager;
	
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

	public final Flux<Event> handle(CommandContext context, T command) {
		return Mono.just(of(context, command))
				.doFinally(onFinally(context, command))
				.handle(this::validate)
				// if exception in the validation -> skip the eventLoop
				.filterWhen( t -> Mono.just(true))
				.flatMap(tp -> Mono.just(tp)
						.publishOn(eventLoop.get(command))
						.handle(this::storeAndEvolution)
						.publishOn(eventLoop.post()))
				.handle(this::afterEventStore)
				.flatMapMany(Flux::fromIterable)
				;
	}

	private void afterEventStore(Tuple2<CommandContext, ImmutableList<Event>> events, SynchronousSink<ImmutableList<Event>> sink) {

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



	private void validate(Tuple2<CommandContext,T> tuple , SynchronousSink<Tuple2<CommandContext,T>> sink) {
		try (Span ignored = this.tracer.start("validate")) {
			ImmutableList<ConstraintViolation> constraintViolations = this.validator.validate(tuple.getT1(), tuple.getT2());
			if (!constraintViolations.isEmpty()) {
				sink.error(new CommandValidationException(constraintViolations, tuple.getT2()));
				return;
			}
			ImmutableList<ConstraintViolation> consistencyValidation;
			try (Transaction tx = this.transactionManager.newTransactionReadOnly()) {
				// validate the command
				consistencyValidation = consistencyValidation(tuple.getT1(), tuple.getT2());
				tx.commit();
			}
			if (!consistencyValidation.isEmpty()) {
				sink.error(new CommandValidationException(consistencyValidation, tuple.getT2()));
				return;
			}

		} catch (Exception exception) {
			sink.error(exception);
			return;
		}

		sink.next(tuple);
	}

	private void storeAndEvolution(Tuple2<CommandContext,T> tuple , SynchronousSink<Tuple2<CommandContext, ImmutableList<Event>>> sink) {
		ImmutableList<Event> events;
		try (Span ignored = this.tracer.start("storeAndEvolution")) {
			events = doStoreAndEvolution(tuple);
		} catch (EventstormRepositoryException cause) {
			sink.error(cause);
			return;
		}
		sink.next(of(tuple.getT1(), events));
	}
	
	private ImmutableList<Event> doStoreAndEvolution(Tuple2<CommandContext, T> tuple) {
		ImmutableList<Event> events;
		try (Transaction tx = this.transactionManager.newTransaction(DEFAULT_TRANSACTION_DEFINITION)) {
			ImmutableList<EventCandidate<?>> candidates;
			try (Span ignored = this.tracer.start("decision")) {
				// apply the decision function (state,command) => events
				candidates = decision(tuple.getT1(), tuple.getT2());
			}

			try (Span ignored = this.tracer.start("store")) {
				// save the to the eventStore
				events = store(candidates);
			}

			try (Span ignored = this.tracer.start("evolution")) {
				// apply the evolution function (state,Event) => State
				events.forEach(evolutionHandlers::on);
			}

			tx.commit();
		}
		return events;
	}

	private ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store [{}]", candidates);
		}
		
		String correlation = candidates.size() > 1 ? UUID.randomUUID().toString() : null;
		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		candidates.forEach(candidate -> builder.add(this.eventStore.appendToStream(
				candidate.getStream(),
				candidate.getStreamId(),
				correlation,
				candidate.getMessage())
			));
		return builder.build();
	}

	private void publish(ImmutableList<Event> events) {
		try (Span ignored = this.tracer.start("publish")) {
			eventBus.publish(events);
		}
	}

	protected ImmutableList<ConstraintViolation> consistencyValidation(CommandContext context, T command) {
		return ImmutableList.of();
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


}