package eu.eventstorm.core.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBus;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventBus implements EventBus {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventBus.class);
	
	private final com.google.common.eventbus.EventBus bus;
	
	public InMemoryEventBus() {
		this.bus = new com.google.common.eventbus.EventBus();
		this.bus.register(this);
	}
	
	@Override
	public void publish(Event event) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("InMemoryEventBus.publish({})", event);
		}
		
		bus.post(event);
		
	}

	
	@Subscribe
	public void listener(Event event) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("InMemoryEventBus.listener({})", event);
		}
		
		

	}
}
