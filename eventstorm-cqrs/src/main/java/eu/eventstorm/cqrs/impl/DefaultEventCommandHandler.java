package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.eventstore.EventStoreClient;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultEventCommandHandler<T extends Command> extends AbstractEventCommandHandler<T> {

	@Autowired
	private EvolutionHandlers evolutionHandlers;
	
	@Autowired
	private EventStoreClient eventStoreClient;
	
	private final Class<T> type;
	
	private final Validator<T> validator;
	
	protected DefaultEventCommandHandler(Class<T> type, Validator<T> validator) {
		this.type = type;
		this.validator = validator;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}

	@Override
	protected void validate(CommandContext context, T command) {

		this.validator.validate(context, command);

		if (context.hasConstraintViolation()) {
			throw new CommandValidationException(context);
		}

		consistencyValidation(context, command);

		if (context.hasConstraintViolation()) {
			throw new CommandValidationException(context);
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

	protected void consistencyValidation(CommandContext commandContext, T command) {
		// nothing to do.
	}

	@Override
	protected void publish(ImmutableList<Event> events) {
		// nothing to do.
	}


}