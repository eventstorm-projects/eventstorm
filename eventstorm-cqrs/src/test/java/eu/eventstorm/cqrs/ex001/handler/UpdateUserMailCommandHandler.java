package eu.eventstorm.cqrs.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.cqrs.AbstractCommandHandler;
import eu.eventstorm.cqrs.ex001.command.UpdateUserMailCommand;
import eu.eventstorm.cqrs.validation.Validators;
import eu.eventstorm.eventstore.EventStoreClient;
import reactor.core.publisher.Flux;

public class UpdateUserMailCommandHandler extends AbstractCommandHandler<UpdateUserMailCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	public UpdateUserMailCommandHandler(EventStoreClient eventStore) {
		super(UpdateUserMailCommand.class, Validators.empty(), eventStore);
	}

	@Override
	protected Flux<Event<EventPayload>> doHandleAfterValidation(UpdateUserMailCommand command) {
		return Flux.empty();
	}

}
