package eu.eventstorm.cqrs.ex001;

import static eu.eventstorm.core.id.StreamIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.core.id.StreamIdGeneratorFactory;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.CommandHandlerRegistry;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.gen.domain.UserDomainHandlerImpl;
import eu.eventstorm.cqrs.ex001.gen.impl.CreateUserCommandImpl;
import eu.eventstorm.cqrs.ex001.handler.CreateUserCommandHandler;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventbus.InMemoryEventBus;
import eu.eventstorm.eventstore.EventStoreClient;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.memory.InMemoryEventStore;
import eu.eventstorm.eventstore.memory.InMemoryEventStoreClient;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import reactor.core.scheduler.Schedulers;

@ExtendWith(LoggerInstancePostProcessor.class)
class Ex001Test {

	private CommandGateway gateway;
	private EventStoreClient eventStoreClient;

	@BeforeEach
	void before() {
		EventBus eventBus = InMemoryEventBus.builder().add(new UserDomainHandlerImpl()).build();
		
		StreamManager manager = new InMemoryStreamManagerBuilder()
				.withDefinition("user")
					.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), () -> UserCreatedEventPayload.newBuilder())
				.and()
				.build();
		
		eventStoreClient = new InMemoryEventStoreClient(manager, new InMemoryEventStore());

		StreamIdGenerator userGenerator = StreamIdGeneratorFactory.inMemoryInteger();

		CommandHandlerRegistry registry = CommandHandlerRegistry.newBuilder()
		        .add(new CreateUserCommandHandler(eventStoreClient, userGenerator))
		        // .add(CreateUserCommandImpl.class, new CreateUserCommandHandler(eventStore,
		        // eventBus))
		        .build();

		gateway = new CommandGateway(Schedulers.elastic(), registry, eventBus);
	}
	
	@Test
	void test() {

		CreateUserCommand command = new CreateUserCommandImpl();
		command.setAge(39);
		command.setName("Jacques");
		command.setEmail("jm@mail.org");

		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		gateway.dispatch(command).doOnNext(event -> builder.add(event)).blockLast();
;		
		assertEquals(1, eventStoreClient.readStream("user", from(1)).count());
		Event event = eventStoreClient.readStream("user", from(1)).findFirst().get();

		assertEquals("user", event.getStream());
		assertEquals("1", event.getStreamId());

		UserCreatedEventPayload userCreatedEvent;
		try {
			userCreatedEvent = event.getData().unpack(UserCreatedEventPayload.class);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		assertEquals("Jacques", userCreatedEvent.getName());
		assertEquals("jm@mail.org", userCreatedEvent.getEmail());
		assertEquals(39, userCreatedEvent.getAge());
	}
	
	@Test
	void testValidationException() {

		CreateUserCommand command = new CreateUserCommandImpl();
		command.setAge(39);
		command.setName("Jacques");
		command.setEmail("jm@FAKE.org");

		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		
		CommandValidationException cve = assertThrows(CommandValidationException.class, () -> gateway.dispatch(command).doOnNext(event -> builder.add(event)).blockLast());
		assertEquals(command, cve.getCommand());
		assertEquals("mail", cve.getConstraintViolations().get(0).getProperties().get(0));
	}
}
