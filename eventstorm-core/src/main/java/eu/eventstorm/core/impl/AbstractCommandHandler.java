package eu.eventstorm.core.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.CommandHandler;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.Validator;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class AbstractCommandHandler<T extends Command> implements CommandHandler<T> {

	private final EventStore eventStore;
	
	private final Validator<T> validator;
	
	private final Class<T> type;

	public AbstractCommandHandler(Class<T> type, Validator<T> validator, EventStore eventStore) {
		this.type = type;
		this.validator = validator;
		this.eventStore = eventStore;
	}

	protected EventStore getEventStore() {
		return eventStore;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}
	
	public final ImmutableList<Event<EventPayload>> handle(T command) {
		
		ImmutableList<ConstraintViolation> constraintViolations = this.validator.validate(command);
		
		if (!constraintViolations.isEmpty()) {
			throw this.validator.createNewException(constraintViolations, command);
		}
		
		return doHandleAfterValidation(command);
	}
	
	protected abstract ImmutableList<Event<EventPayload>> doHandleAfterValidation(T command);


}