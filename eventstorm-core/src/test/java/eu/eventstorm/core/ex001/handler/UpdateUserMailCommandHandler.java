package eu.eventstorm.core.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.ex001.command.UpdateUserMailCommand;
import eu.eventstorm.core.impl.AbstractCommandHandler;

public class UpdateUserMailCommandHandler extends AbstractCommandHandler<UpdateUserMailCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	public UpdateUserMailCommandHandler(EventStore eventStore) {
		super(UpdateUserMailCommand.class, eventStore);
	}

	@Override
	public ImmutableList<Event<?>> handle(UpdateUserMailCommand command) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("handle ({})", command);
		}

		//1. validate on master data.
		
		//2.
	//	UserCreatedEventImpl event = new UserCreatedEventImpl();

		//getEventStore().store(new UserAggregateId(), event);
		
		//event.applyOn(domainModel);

		//getEventBus().publish(event);
		
		return ImmutableList.of();
	}

}
