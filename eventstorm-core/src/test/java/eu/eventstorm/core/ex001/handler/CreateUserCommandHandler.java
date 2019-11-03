package eu.eventstorm.core.ex001.handler;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.CommandHandler;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.annotation.CqrsCommandHandler;
import eu.eventstorm.core.ex001.command.CreateUserCommand;
import eu.eventstorm.core.ex001.gen.event.UserCreatedEvent;

@CqrsCommandHandler(command = CreateUserCommand.class)
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserCommandHandler.class);

	private final EventStore eventStore;
	
	public CreateUserCommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public Event handle(CreateUserCommand command) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("handle ({})", command);
		}

		// 1. Validate ....
		Event event = new UserCreatedEvent("", OffsetDateTime.now(), "type" , "contentType" );
		
		this.eventStore.store(event);
		
		return event;
	}

}
