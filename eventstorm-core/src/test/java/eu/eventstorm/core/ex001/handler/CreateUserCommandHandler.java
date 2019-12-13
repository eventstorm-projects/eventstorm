package eu.eventstorm.core.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.ex001.command.CreateUserCommand;
import eu.eventstorm.core.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.core.ex001.gen.event.UserCreatedEventPayloadImpl;
import eu.eventstorm.core.id.AggregateIdGenerator;
import eu.eventstorm.core.impl.AbstractCommandHandler;

public final class CreateUserCommandHandler extends AbstractCommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	private final AggregateIdGenerator aig;
	
	public CreateUserCommandHandler(EventStore eventStore, AggregateIdGenerator aig) {
		super(CreateUserCommand.class, eventStore);
		this.aig = aig;
	}

	@Override
	public ImmutableList<Event<?>> handle(CreateUserCommand command) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("handle ({})", command);
		}

		//1. validate on master data.
		
		
		AggregateId id = this.aig.generate();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("generate ({})", id);
		}
		
		UserCreatedEventPayload eventData = new UserCreatedEventPayloadImpl(
				command.getName(),
				command.getEmail(),
				command.getAge()
				);

		Event<?> event = getEventStore().appendToStream("user", id, eventData);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("event ({})", event);
		}
		
		return ImmutableList.of(event);
		
	}

}
