package eu.eventstorm.core.eventbus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.mockito.Mockito;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

class DualEventBusTest {

	@Test
	@SuppressWarnings("unchecked")
	void testDualInMemory() {

		AtomicInteger integer = new AtomicInteger(0);
		Consumer<Event<? extends EventPayload>> consumer = event -> {
			integer.incrementAndGet();
		};

		InMemoryEventBus one = new InMemoryEventBus(ImmutableList.of(consumer));
		InMemoryEventBus two = new InMemoryEventBus(ImmutableList.of(consumer));
		DualEventBus dualEventBus = new DualEventBus(one, two);

		assertEquals(0, integer.get());

		dualEventBus.publish(Mockito.mock(Event.class));

		assertEquals(2, integer.get());

	}

}
