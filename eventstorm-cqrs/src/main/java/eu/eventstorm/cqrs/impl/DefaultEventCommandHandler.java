package eu.eventstorm.cqrs.impl;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
public abstract class DefaultEventCommandHandler<T extends Command> extends AbstractEventCommandHandler<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventCommandHandler.class);

	@Autowired
	private EvolutionHandlers evolutionHandlers;
	
	@Autowired
	private EventStoreClient eventStoreClient;
	
	private final Class<T> type;
	
	private final Validator<T> validator;
	
	public DefaultEventCommandHandler(Class<T> type, Validator<T> validator) {
		this.type = type;
		this.validator = validator;
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
	protected ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates) {
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