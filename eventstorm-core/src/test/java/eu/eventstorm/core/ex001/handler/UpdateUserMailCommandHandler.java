package eu.eventstorm.core.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.ex001.command.UpdateUserMailCommand;
import eu.eventstorm.core.impl.AbstractCommandHandler;
import eu.eventstorm.core.validation.Validators;

public class UpdateUserMailCommandHandler extends AbstractCommandHandler<UpdateUserMailCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	public UpdateUserMailCommandHandler(EventStore eventStore) {
		super(UpdateUserMailCommand.class, Validators.empty(), eventStore);
	}

	@Override
	protected ImmutableList<Event<EventPayload>> doHandleAfterValidation(UpdateUserMailCommand command) {
		return ImmutableList.of();
	}

}
