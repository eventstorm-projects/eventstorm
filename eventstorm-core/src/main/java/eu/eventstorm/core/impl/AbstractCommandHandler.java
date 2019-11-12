package eu.eventstorm.core.impl;

import eu.eventstorm.core.EventBus;
import eu.eventstorm.core.EventStore;

public class AbstractCommandHandler {

private final EventStore eventStore;
	
	private final EventBus eventBus;
	
	public AbstractCommandHandler(EventStore eventStore, EventBus eventBus) {
		this.eventStore = eventStore;
		this.eventBus = eventBus;
	}

	protected EventStore getEventStore() {
		return eventStore;
	}

	protected EventBus getEventBus() {
		return eventBus;
	}
	
	
}
