package eu.eventstorm.core.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.CommandHandler;
import eu.eventstorm.core.EventBus;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.ex001.command.CreateUserCommand;
import eu.eventstorm.core.ex001.event.UserCreatedEvent;
import eu.eventstorm.core.ex001.gen.domain.UserAggregateId;
import eu.eventstorm.core.ex001.gen.event.UserCreatedEventImpl;
import eu.eventstorm.core.impl.AbstractCommandHandler;

public class UpdateUserMailCommandHandler extends AbstractCommandHandler implements CommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	public UpdateUserMailCommandHandler(EventStore eventStore, EventBus eventBus) {
		super(eventStore, eventBus);
	}

	@Override
	public void handle(CreateUserCommand command) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("handle ({})", command);
		}

		//1. validate on master data.
		
		//2.
		UserCreatedEventImpl event = new UserCreatedEventImpl();

		getEventStore().store(new UserAggregateId(), event);
		
		//event.applyOn(domainModel);

		getEventBus().publish(event);
	}

}
