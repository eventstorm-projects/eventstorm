package eu.eventstorm.cqrs.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.EventCandidate;
import eu.eventstorm.eventstore.EventStoreClient;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractCommandHandler<T extends Command> implements CommandHandler<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandHandler.class);

	private final EventBus eventBus;
	
	private final EventStoreClient eventStoreClient;
	
	private final Validator<T> validator;
	
	private final Class<T> type;

	public AbstractCommandHandler(Class<T> type, Validator<T> validator, EventStoreClient eventStoreClient, EventBus eventBus) {
		this.type = type;
		this.validator = validator;
		this.eventStoreClient = eventStoreClient;
		this.eventBus = eventBus;
	}

	protected EventStoreClient getEventStoreClient() {
		return eventStoreClient;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}
	
	protected final EventBus getEventBus() {
		return this.eventBus;
	}
	
	protected final void validate(T command) {
		
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
	
	protected ImmutableList<ConstraintViolation> consistencyValidation(T command) {
		return ImmutableList.of();
	}

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate> decision(T command);

	/**
	 *  (state,Event) => State
	 */
	protected abstract void evolution(ImmutableList<Event> events);
	

}