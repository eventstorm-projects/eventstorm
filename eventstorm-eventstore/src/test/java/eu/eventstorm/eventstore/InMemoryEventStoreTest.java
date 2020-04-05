package eu.eventstorm.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.eventstore.memory.InMemoryEventStore;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class InMemoryEventStoreTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	void test() {
		
		StreamDefinition sd = mock(StreamDefinition.class);
		when(sd.getName()).thenReturn("toto");
		
		Deserializer deserializer = mock(Deserializer.class);
		
		StreamEvantPayloadDefinition<?> sepd = mock(StreamEvantPayloadDefinition.class);
		when(sepd.getPayloadDeserializer()).thenReturn(deserializer);
		when(sepd.getStream()).thenReturn("toto");

		InMemoryEventStore eventStore = new InMemoryEventStore();
		
		EventStoreException ex = assertThrows(EventStoreException.class, () -> eventStore.readStream(sd, "12"));
		assertEquals(EventStoreException.Type.STREAM_NOT_FOUND, ex.getType());
		
		Event<?> event = eventStore.appendToStream(sepd, "12", new byte[0]);
		
		assertEquals(1, eventStore.readStream(sd, "12").count());
		assertEquals(event, eventStore.readStream(sd, "12").findFirst().get());
		assertEquals(0, eventStore.readStream(sd, "13").count());
	}
}
