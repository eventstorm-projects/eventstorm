package eu.eventstorm.core.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.id.AggregateIds;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class InMemoryEventStoreTest {

	@Test
	void test() {

		InMemoryEventStore eventStore = new InMemoryEventStore();
		EventStoreException ex = assertThrows(EventStoreException.class, () -> eventStore.readStream("fake", AggregateIds.from(12)));
		assertEquals(EventStoreException.Type.STREAM_NOT_FOUND, ex.getType());
		
		Event event = eventStore.appendToStream("toto", AggregateIds.from(12), new EventPayload() {
		});
		
		assertEquals(1, eventStore.readStream("toto", AggregateIds.from(12)).count());
			
		assertEquals(event, eventStore.readStream("toto", AggregateIds.from(12)).findFirst().get());
		assertEquals(0, eventStore.readStream("toto", AggregateIds.from(13)).count());
	}
}
