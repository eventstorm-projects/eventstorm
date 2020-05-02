package eu.eventstorm.cqrs.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.StreamId;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.cqrs.AbstractCommandHandler;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.validator.CreateUserCommandValidator;
import eu.eventstorm.eventstore.EventStoreClient;
import reactor.core.publisher.Flux;

public final class CreateUserCommandHandler extends AbstractCommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	private final StreamIdGenerator aig;
	
	public CreateUserCommandHandler(EventStoreClient eventStore, StreamIdGenerator aig) {
		super(CreateUserCommand.class, new CreateUserCommandValidator(), eventStore);
		this.aig = aig;
	}

	@Override
	protected Flux<Event> doHandleAfterValidation(CreateUserCommand command) {
			
		StreamId id = this.aig.generate();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("generate ({})", id);
		}
		
		UserCreatedEventPayload payload = UserCreatedEventPayload.newBuilder()
			.setName(command.getName())
			.setEmail(command.getEmail())
			.setAge(command.getAge())
			.build();

		Event event = getEventStoreClient().appendToStream("user", id, payload);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("event ({})", event);
		}
		
		return Flux.just(event);
		
	}

	

}
