package eu.eventstorm.cqrs.ex001;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandException;
import eu.eventstorm.cqrs.CommandGatewayException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.context.DefaultCommandContext;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.gen.impl.CreateUserCommandImpl;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.eventstore.EventStoreClient;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Ex001Configuration.class })
@ExtendWith(LoggerInstancePostProcessor.class)
class Ex001Test {

	@Autowired
	private CommandGateway gateway;
	
	@Autowired
	private EventStoreClient eventStoreClient;

	@Test
	void test() {

		CreateUserCommand command = new CreateUserCommandImpl();
		command.setAge(39);
		command.setName("Jacques");
		command.setEmail("jm@mail.org");

		ImmutableList.Builder<Event> builder = ImmutableList.builder();
		gateway.<CreateUserCommand,Event>dispatch(new DefaultCommandContext(), command).doOnNext(builder::add).blockLast();
		
		assertEquals(1, eventStoreClient.readStream("user", "1").count());
		Event event = eventStoreClient.readStream("user", "1").findFirst().get();

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
		
		CommandValidationException cve = assertThrows(CommandValidationException.class, () -> gateway.<CreateUserCommand, Event>dispatch(new DefaultCommandContext(),command).doOnNext(builder::add).blockLast());
		assertEquals(command, cve.getCommand());
		assertEquals("mail", cve.getConstraintViolations().get(0).getProperties().get(0));
	}

	@Test
	void testCommandExceptionException() {
		CommandGatewayException cge = assertThrows(CommandGatewayException.class, () -> gateway.<Command, Event>dispatch(new DefaultCommandContext(),new Command(){}).blockLast());
		assertEquals(CommandGatewayException.Type.NOT_FOUND, cge.getType());
	}
}
