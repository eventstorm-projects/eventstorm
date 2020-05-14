package eu.eventstorm.cqrs.impl;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventstore.EventStoreClient;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultCommandHandler<T extends Command> extends AbstractCommandHandler<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCommandHandler.class);

	private final EvolutionHandlers evolutionHandlers;
	
	private final EventStoreClient eventStoreClient;
	
	private final Validator<T> validator;
	
	private final Class<T> type;
	
	public DefaultCommandHandler(Class<T> type, 
			Validator<T> validator, 
			EventStoreClient eventStoreClient, 
			EvolutionHandlers evolutionHandlers) {
		
		this.type = type;
		this.validator = validator;
		this.eventStoreClient = eventStoreClient;
		this.evolutionHandlers = evolutionHandlers;
	}
	

	@Override
	public final Class<T> getType() {
		return this.type;
	}

	@Override
	protected void validate(T command) {
		
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

	@Override
	protected ImmutableList<Event> store(ImmutableList<EventCandidate> candidates) {
		return this.eventStoreClient.appendToStream(candidates).collect(toImmutableList());
	}

	@Override
	protected void evolution(ImmutableList<Event> events) {
		events.forEach(evolutionHandlers::on);
	}

	protected ImmutableList<ConstraintViolation> consistencyValidation(T command) {
		return ImmutableList.of();
	}


	@Override
	protected void publish(ImmutableList<Event> events) {
		// nothing to do.
	}
	
	
	

}