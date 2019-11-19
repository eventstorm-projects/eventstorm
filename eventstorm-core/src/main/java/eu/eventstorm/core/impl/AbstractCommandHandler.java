package eu.eventstorm.core.impl;

import eu.eventstorm.core.EventStore;

public class AbstractCommandHandler {

private final EventStore eventStore;
	
	public AbstractCommandHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	protected EventStore getEventStore() {
		return eventStore;
	}

}
