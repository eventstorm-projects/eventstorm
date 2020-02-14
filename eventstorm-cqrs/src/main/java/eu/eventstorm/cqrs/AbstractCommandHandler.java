package eu.eventstorm.cqrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventstore.EventStore;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class AbstractCommandHandler<T extends Command> implements CommandHandler<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandHandler.class);

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
	
	public final Flux<Event<EventPayload>> handle(T command) {
		
		ImmutableList<ConstraintViolation> constraintViolations = this.validator.validate(command);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Validation of [{}] -> [{}]", command, constraintViolations);
		}
		
		if (!constraintViolations.isEmpty()) {
			throw new CommandValidationException(constraintViolations, command);
		}
		
		return doHandleAfterValidation(command);
	}
	
	protected abstract Flux<Event<EventPayload>> doHandleAfterValidation(T command);


}