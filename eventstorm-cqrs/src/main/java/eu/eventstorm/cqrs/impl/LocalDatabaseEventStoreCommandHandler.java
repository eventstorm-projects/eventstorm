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
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.db.DatabaseEventStore;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class LocalDatabaseEventStoreCommandHandler<T extends Command>  implements CommandHandler<T, Event> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDatabaseEventStoreCommandHandler.class);

	private final Class<T> type;

	private final Validator<T> validator;
	
	@Autowired
	private DatabaseEventStore eventStore;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private StreamManager streamManager;
	
	@Autowired
	private EvolutionHandlers evolutionHandlers;
	
	public LocalDatabaseEventStoreCommandHandler(Class<T> type, Validator<T> validator) {
		this.type = type;
		this.validator = validator;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}

	public final ImmutableList<Event> handle(T command) {
		
		ImmutableList<Event> events;
		
		try (Transaction tx = this.transactionManager.newTransactionReadWrite()) {
			
			// validate the command
			validate(command);
			
			// apply the decision function (state,command) => events
			ImmutableList<EventCandidate<?>> candidates = decision(command);

			// save the to the eventStore
			events = store(candidates);
			
			// apply the evolution function (state,Event) => State
			evolution(events);
			
			tx.commit();
		}
		
		return events;
	}

	private void validate(T command) {
		
		ImmutableList<ConstraintViolation> constraintViolations = this.validator.validate(command);
		
		ImmutableList<ConstraintViolation> consistencyValidation = consistencyValidation(command);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Validation of [{}] -> [{}] [{}]", command, constraintViolations, consistencyValidation);
		}
		
		if (!constraintViolations.isEmpty() || !consistencyValidation.isEmpty()) {
			throw new CommandValidationException(ImmutableList.<ConstraintViolation>builder()
					.addAll(constraintViolations).addAll(consistencyValidation).build(), command);
		}
	}

	private ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("store [{}]", candidates);
		}
		
		UUID correlation = UUID.randomUUID();
		return candidates.stream()
				.map(candidate -> {
					
					StreamDefinition sd = streamManager.getDefinition(candidate.getStream());
					
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Stream definition for [{}] -> [{}]", candidate.getStream(), sd);
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

	protected ImmutableList<ConstraintViolation> consistencyValidation(T command) {
		return ImmutableList.of();
	}

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate<?>> decision(T command);
	

}