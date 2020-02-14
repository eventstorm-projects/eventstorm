package eu.eventstorm.cqrs.ex001;

import static eu.eventstorm.core.id.AggregateIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.id.AggregateIdGenerator;
import eu.eventstorm.core.id.AggregateIdGeneratorFactory;
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
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.memory.InMemoryEventStore;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import reactor.core.scheduler.Schedulers;

@ExtendWith(LoggerInstancePostProcessor.class)
class Ex001Test {

	private CommandGateway gateway;
	private EventStore eventStore;

	@BeforeEach
	void before() {
		EventBus eventBus = InMemoryEventBus.builder().add(new UserDomainHandlerImpl()).build();
		
		eventStore = new InMemoryEventStore();

		AggregateIdGenerator userGenerator = AggregateIdGeneratorFactory.inMemoryInteger();

		CommandHandlerRegistry registry = CommandHandlerRegistry.newBuilder()
		        .add(new CreateUserCommandHandler(eventStore, userGenerator))
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

		ImmutableList.Builder<Event<EventPayload>> builder = ImmutableList.builder();
		gateway.dispatch(command).doOnNext(event -> builder.add(event)).blockLast();
;		
		assertEquals(1, eventStore.readStream("user", from(1)).count());
		Event event = eventStore.readStream("user", from(1)).findFirst().get();

		assertEquals("user", event.getAggregateType());
		assertEquals(from(1), event.getAggregateId());

		UserCreatedEventPayload userCreatedEvent = UserCreatedEventPayload.class.cast(event.getPayload());

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

		ImmutableList.Builder<Event<EventPayload>> builder = ImmutableList.builder();
		
		CommandValidationException cve = assertThrows(CommandValidationException.class, () -> gateway.dispatch(command).doOnNext(event -> builder.add(event)).blockLast());
		assertEquals(command, cve.getCommand());
		assertEquals("mail", cve.getConstraintViolations().get(0).getProperties().get(0));
	}
}
