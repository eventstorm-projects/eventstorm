package eu.eventstorm.eventstore.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.core.id.StreamIds;
import eu.eventstorm.eventstore.EventStoreException;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class InMemoryEventStoreClientTest {

	private StreamManager streamManager;
	
	@BeforeEach
	void beforeEach() {
		streamManager = new InMemoryStreamManagerBuilder()
				.withDefinition("test")
					.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.parser())
				.and()
				.build();
	}
	
	@Test
	void test() {
		
		InMemoryEventStoreClient client = new InMemoryEventStoreClient(streamManager, new InMemoryEventStore());

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
