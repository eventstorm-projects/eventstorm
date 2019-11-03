package eu.eventstorm.core.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBus;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventBus implements EventBus {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventBus.class);
	
	@Override
	public void publish(Event event) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("InMemoryEventBys.publish({})", event);
		}
		
	}

}
