package eu.eventstorm.eventstore;

import eu.eventstorm.eventstore.memory.InMemoryEventStore;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class InMemoryEventStoreTest extends EventStoreTest {

	@Override
	protected EventStore initEventStore() {
		return new InMemoryEventStore(new EventStoreProperties());
	}
}
