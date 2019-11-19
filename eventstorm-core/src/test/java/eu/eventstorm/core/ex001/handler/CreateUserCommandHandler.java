package eu.eventstorm.core.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.CommandHandler;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.ex001.command.CreateUserCommand;
import eu.eventstorm.core.ex001.event.UserCreatedEvent;
import eu.eventstorm.core.ex001.gen.event.UserCreatedEventImpl;
import eu.eventstorm.core.id.AggregateIdGenerator;
import eu.eventstorm.core.impl.AbstractCommandHandler;

public class CreateUserCommandHandler extends AbstractCommandHandler implements CommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	private final AggregateIdGenerator aig;
	
	public CreateUserCommandHandler(EventStore eventStore, AggregateIdGenerator aig) {
		super(eventStore);
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
		
		UserCreatedEvent eventData = new UserCreatedEventImpl(
				command.getName(),
				command.getEmail(),
				command.getAge()
				);

		Event<UserCreatedEvent> event = getEventStore().appendToStream("user", id, eventData);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("event ({})", event);
		}
		
		return ImmutableList.of(event);
		
	}


}
