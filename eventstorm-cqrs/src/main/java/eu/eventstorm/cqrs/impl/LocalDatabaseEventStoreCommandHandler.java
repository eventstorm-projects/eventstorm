package eu.eventstorm.cqrs.impl;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.UUID;

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
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
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
	
	public LocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator) {
		this.type = type;
		this.validator = validator;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}

	public final Flux<Event> handle(CommandContext context, T command) {
		
		
		
		try (Transaction tx = this.transactionManager.newTransactionReadOnly()) {
			// validate the command
			validate(context, command);
			
			tx.rollback();
		}
		
		return Mono.just(Tuples.of(context, command))
				.publishOn(eventLoop.get(command))
				.map(tuple -> {
					ImmutableList<Event> events;
					try (Transaction tx = this.transactionManager.newTransactionReadWrite()) {
						
						// apply the decision function (state,command) => events
						ImmutableList<EventCandidate<?>> candidates = decision(tuple.getT1(), tuple.getT2());
						
						// save the to the eventStore
						events = store(candidates);
						
						// apply the evolution function (state,Event) => State
						evolution(events);
						
						tx.commit();
					}
					return events;
				})
				.map(events -> {
					eventBus.publish(events);
					return events;
				})
				.flatMapMany(Flux::fromIterable);
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
		return candidates.stream()
				.map(candidate -> {
					
					StreamDefinition sd = streamManager.getDefinition(candidate.getStream());
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Stream definition for [{}] -> [{}]", candidate.getStream(), sd);
					}
					
					if (sd == null) {
						throw new IllegalStateException("No defintion found for stream [" + candidate.getStream() + "]");
					}
					
					return this.eventStore.appendToStream(
							sd.getStreamEventDefinition(candidate.getMessage().getClass().getSimpleName()), 
							candidate.getStreamId().toStringValue(), 
							correlation, 
							candidate.getMessage())
						;
				})
				.collect(toImmutableList());
	}

	private void evolution(ImmutableList<Event> events) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("evolution [{}]", events);
		}
		events.forEach(evolutionHandlers::on);
	}

	protected ImmutableList<ConstraintViolation> consistencyValidation(CommandContext context, T command) {
		return ImmutableList.of();
	}

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate<?>> decision(CommandContext context, T command);
	

}