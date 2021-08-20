package eu.eventstorm.eventstore;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class EventStoreTest {
	
	private EventStore eventStore;
	
	@BeforeEach
	void init() throws Exception {
		
		StreamManager manager = new InMemoryStreamManagerBuilder()
				.withDefinition("user")
				.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), UserCreatedEventPayload::newBuilder)
			.and()
			.build();
		
		eventStore = initEventStore(manager);
	}
	
	@Test
	void testAppend() throws Exception {
		
		eventStore.appendToStream(new EventCandidate<>("user",
				"1",   UserCreatedEventPayload.newBuilder()
					.setAge(39)
					.setName("ja")
					.setEmail("gmail")
					.build()), null);

		try (Stream<Event> stream = eventStore.readStream("user", "1")) {
			Optional<Event> op = stream.findFirst();
			assertTrue(op.isPresent());
			
			UserCreatedEventPayload payload = op.get().getData().unpack(UserCreatedEventPayload.class);
			assertEquals("ja", payload.getName());
			assertEquals("gmail", payload.getEmail());
			assertEquals(39, payload.getAge());	
		}
		
	}
	
//	@Test
//	void testEventStoreException() {
//		StreamManager manager = new InMemoryStreamManagerBuilder()
//				.withDefinition("user")
//				.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.parser())
//			.and()
//			.build();
//		
//		
//		StreamEventDefinition def = manager.getDefinition("user").getStreamEventDefinition(UserCreatedEventPayload.class.getSimpleName());
//		
//		EventStoreException ese = assertThrows(EventStoreException.class , () -> eventStore.appendToStream(def, "1", am));
//		assertEquals(EventStoreException.Type.FAILED_TO_SERILIAZE_PAYLOAD, ese.getType());
//	}
	
	protected abstract EventStore initEventStore(StreamManager manager);

}
