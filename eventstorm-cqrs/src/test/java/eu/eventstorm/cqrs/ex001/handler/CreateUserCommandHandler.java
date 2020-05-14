package eu.eventstorm.cqrs.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.StreamId;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.validator.CreateUserCommandValidator;
import eu.eventstorm.cqrs.impl.DefaultCommandHandler;
import eu.eventstorm.eventstore.EventStoreClient;

public final class CreateUserCommandHandler extends DefaultCommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	private final StreamIdGenerator aig;
	
	public CreateUserCommandHandler(EventStoreClient eventStore, EvolutionHandlers evolutionHandlers, StreamIdGenerator aig) {
		super(CreateUserCommand.class, new CreateUserCommandValidator(), eventStore, evolutionHandlers);
		this.aig = aig;
	}

	@Override
	protected ImmutableList<EventCandidate> decision(CreateUserCommand command) {
		
		StreamId id = this.aig.generate();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("generate ({})", id);
		}
		
		UserCreatedEventPayload payload = UserCreatedEventPayload.newBuilder()
			.setName(command.getName())
			.setEmail(command.getEmail())
			.setAge(command.getAge())
			.build();
		
		return ImmutableList.of(new EventCandidate("user", id, payload));
		
	}


}