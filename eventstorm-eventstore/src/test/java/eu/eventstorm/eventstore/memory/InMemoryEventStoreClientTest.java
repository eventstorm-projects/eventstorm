package eu.eventstorm.eventstore.memory;

import static eu.eventstorm.eventstore.ex.UserCreatedEventPayload.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
				.withPayload(UserCreatedEventPayload.class, getDescriptor(), parser(), UserCreatedEventPayload::newBuilder)
			.and()
			.build();
	}
	
	@Test
	void test() {
		
		InMemoryEventStoreClient client = new InMemoryEventStoreClient(manager, new InMemoryEventStore(new EventStoreProperties()));

		client.appendToStream("test", "1", newBuilder().build());
		client.appendToStream("test", "2", newBuilder().build());
		client.appendToStream("test", "1", newBuilder().build());
		client.appendToStream("test", "2", newBuilder().build());
		client.appendToStream("test", "2", newBuilder().build());
		
		assertEquals(2, client.readStream("test", "1").count());
		assertEquals(3, client.readStream("test", "2").count());
		
		EventStoreException ese = assertThrows(EventStoreException.class, () -> client.appendToStream("fake", "1", newBuilder().build()));
		assertEquals(EventStoreException.Type.STREAM_NOT_FOUND, ese.getType());
	}
	
}
