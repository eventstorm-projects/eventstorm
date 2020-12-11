package eu.eventstorm.cqrs.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import brave.Span;
import brave.Tracer;
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
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.util.tuple.Tuple2;
import eu.eventstorm.util.tuple.Tuples;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class LocalDatabaseEventStoreCommandHandler<T extends Command> implements CommandHandler<T, Event> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDatabaseEventStoreCommandHandler.class);

	private final Class<T> type;

	private final Validator<T> validator;
	
	@Autowired
	private LocalDatabaseEventStore eventStore;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private StreamManager streamManager;
	
	@Autowired
	private EvolutionHandlers evolutionHandlers;
	
	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private EventLoop eventLoop;

	@Autowired
	private Tracer tracer;
	
	public LocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator) {
		this.type = type;
		this.validator = validator;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}

	public final Flux<Event> handle(CommandContext context, T command) {
		
		Span newSpan = this.tracer.nextSpan().name("validate");
		newSpan.tag("thread", Thread.currentThread().getName());
		try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(newSpan.start())) {
			try (Transaction tx = this.transactionManager.newTransactionReadOnly()) {
				try {
					// validate the command
					validate(context, command);
				} finally {
					tx.rollback();	
				}
			}	
		} finally {
			newSpan.finish();
		}
		
		return Mono.just(Tuples.of(context, command))
				.publishOn(eventLoop.get(command))
				.map(tuple -> storeAndEvolution(tuple, 0))
				.publishOn(eventLoop.post())
				.doOnNext(this::postStoreAndEvolution)
				.doOnNext(eventBus::publish)
				.flatMapMany(Flux::fromIterable);
	}

	private ImmutableList<Event> storeAndEvolution(Tuple2<CommandContext, T> tuple, int retry) {
		String name = Thread.currentThread().getName();
		Span span = this.tracer.nextSpan().name("eventstore");
		span.tag("thread", name);
		
	    try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
	    	return doStoreAndEvolution(tuple);
		} catch (EventstormRepositoryException cause) {
			LOGGER.info("storeAndEvolution -> retry [{}]", retry);
			span.tag("retry", String.valueOf(retry));
			span.error(cause);
			if (retry > 9) {
				throw cause;
			} else {
				return storeAndEvolution(tuple, retry + 1);	
			}
		} finally {
			span.finish();
		}
		
	}
	
	private ImmutableList<Event> doStoreAndEvolution(Tuple2<CommandContext, T> tuple) {
		ImmutableList<Event> events;
		try (Transaction tx = this.transactionManager.newTransactionReadWrite()) {
			
			ImmutableList<EventCandidate<?>> candidates;
			Span span = this.tracer.nextSpan().name("decision");
			try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
				// apply the decision function (state,command) => events
				candidates = decision(tuple.getT1(), tuple.getT2());
			} finally {
				span.finish();
			}
			
			span = this.tracer.nextSpan().name("store");
			try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
				// save the to the eventStore
				events = store(candidates);
			} finally {
				span.finish();
			}
			
			span = this.tracer.nextSpan().name("evolution");
			try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
				// apply the evolution function (state,Event) => State
				events.forEach(evolutionHandlers::on);
			} finally {
				span.finish();
			}
			
			tx.commit();
		}
		return events;
	}
	
	private void validate(CommandContext context, T command) {
		
		ImmutableList<ConstraintViolation> constraintViolations = this.validator.validate(context, command);
		
		if (!constraintViolations.isEmpty()) {
			throw new CommandValidationException(constraintViolations, command);
		}
		
		ImmutableList<ConstraintViolation> consistencyValidation = consistencyValidation(context, command);
		
		if (!consistencyValidation.isEmpty()) {
			throw new CommandValidationException(consistencyValidation, command);
		}
		
	}

	private ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store [{}]", candidates);
		}
		
		String correlation = candidates.size() > 1 ? UUID.randomUUID().toString() : null;
		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		candidates.forEach(candidate -> {
			StreamDefinition sd = streamManager.getDefinition(candidate.getStream());
					
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stream definition for [{}] -> [{}]", candidate.getStream(), sd);
			}
					
			if (sd == null) {
				throw new IllegalStateException("No defintion found for stream [" + candidate.getStream() + "]");
			}
					
			builder.add(this.eventStore.appendToStream(
					sd.getStreamEventDefinition(candidate.getMessage().getClass().getSimpleName()), 
					candidate.getStreamId(), 
					correlation, 
					candidate.getMessage())
				);
		});
		return builder.build();
	}

	private void postStoreAndEvolution(ImmutableList<Event> events) {
		Span span = this.tracer.nextSpan().name("postStoreAndEvolution");
		span.tag("thread",  Thread.currentThread().getName());
		try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(span.start())) {
			doPostStoreAndEvolution(events);
		} finally {
			span.finish();
		}
	}
	
	protected void doPostStoreAndEvolution(ImmutableList<Event> events) {
	}

	protected ImmutableList<ConstraintViolation> consistencyValidation(CommandContext context, T command) {
		return ImmutableList.of();
	}

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate<?>> decision(CommandContext context, T command);
	
	

}