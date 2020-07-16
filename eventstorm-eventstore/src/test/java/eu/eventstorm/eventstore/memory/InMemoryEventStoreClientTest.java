package eu.eventstorm.eventstore.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.core.id.StreamIds;
import eu.eventstorm.eventstore.EventStoreException;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class InMemoryEventStoreClientTest {

	private StreamManager manager;
	
	@BeforeEach
	void beforeEach() {
		manager = new InMemoryStreamManagerBuilder()
				.withDefinition("test")
				.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), () -> UserCreatedEventPayload.newBuilder())
			.and()
			.build();
	}
	
	@Test
	void test() {
		
		InMemoryEventStoreClient client = new InMemoryEventStoreClient(manager, new InMemoryEventStore(new EventStoreProperties()));

		client.appendToStream("test", StreamIds.from(1), UserCreatedEventPayload.newBuilder().build());
		client.appendToStream("test", StreamIds.from(2), UserCreatedEventPayload.newBuilder().build());
		client.appendToStream("test", StreamIds.from(1), UserCreatedEventPayload.newBuilder().build());
		client.appendToStream("test", StreamIds.from(2), UserCreatedEventPayload.newBuilder().build());
		client.appendToStream("test", StreamIds.from(2), UserCreatedEventPayload.newBuilder().build());
		
		assertEquals(2, client.readStream("test", StreamIds.from(1)).count());
		assertEquals(3, client.readStream("test", StreamIds.from(2)).count());
		
		EventStoreException ese = assertThrows(EventStoreException.class, () -> client.appendToStream("fake", StreamIds.from(1), UserCreatedEventPayload.newBuilder().build()));
		assertEquals(EventStoreException.Type.STREAM_NOT_FOUND, ese.getType());
	}
	
}
