package eu.eventstorm.core.impl;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.CommandHandler;
import eu.eventstorm.core.EventStore;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class AbstractCommandHandler<T extends Command> implements CommandHandler<T> {

	private final EventStore eventStore;
	
	private final Class<T> type;

	public AbstractCommandHandler(Class<T> type, EventStore eventStore) {
		this.type = type;
		this.eventStore = eventStore;
	}

	protected EventStore getEventStore() {
		return eventStore;
	}

	@Override
	public final Class<T> getType() {
		return this.type;
	}

}