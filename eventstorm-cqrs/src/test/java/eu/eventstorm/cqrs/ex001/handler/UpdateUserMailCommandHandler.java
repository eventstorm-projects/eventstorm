package eu.eventstorm.cqrs.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.ex001.command.UpdateUserMailCommand;
import eu.eventstorm.cqrs.impl.DefaultCommandHandler;
import eu.eventstorm.cqrs.validation.Validators;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.EventCandidate;
import eu.eventstorm.eventstore.EventStoreClient;

public class UpdateUserMailCommandHandler extends DefaultCommandHandler<UpdateUserMailCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	public UpdateUserMailCommandHandler(EventStoreClient eventStore, EventBus eventBus) {
		super(UpdateUserMailCommand.class, Validators.empty(), eventStore, eventBus);
	}

	@Override
	protected ImmutableList<EventCandidate> decision(UpdateUserMailCommand command) {
		return ImmutableList.of();
	}

	@Override
	protected void evolution(ImmutableList<Event> events) {
		
	}

}
