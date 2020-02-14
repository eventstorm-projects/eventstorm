package eu.eventstorm.eventbus;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventBus {

	void publish(Event<EventPayload> event);
	
}