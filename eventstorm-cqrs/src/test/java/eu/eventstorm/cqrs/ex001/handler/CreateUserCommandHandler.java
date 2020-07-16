package eu.eventstorm.cqrs.ex001.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.StreamId;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.core.id.StreamIdGeneratorFactory;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.validator.CreateUserCommandValidator;
import eu.eventstorm.cqrs.impl.DefaultEventCommandHandler;

@Component
public final class CreateUserCommandHandler extends DefaultEventCommandHandler<CreateUserCommand> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserMailCommandHandler.class);

	private final StreamIdGenerator aig;
	
	public CreateUserCommandHandler() {
		super(CreateUserCommand.class, new CreateUserCommandValidator());
		this.aig = StreamIdGeneratorFactory.inMemoryInteger();
	}

	@Override
	protected ImmutableList<EventCandidate<?>> decision(CreateUserCommand command) {
		
		StreamId id = this.aig.generate();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("generate ({})", id);
		}
		
		UserCreatedEventPayload payload = UserCreatedEventPayload.newBuilder()
			.setName(command.getName())
			.setEmail(command.getEmail())
			.setAge(command.getAge())
			.build();
		
		return ImmutableList.of(new EventCandidate<>("user", id, payload));
		
	}


}