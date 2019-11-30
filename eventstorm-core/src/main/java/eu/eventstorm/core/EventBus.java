package eu.eventstorm.core;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventBus {

	void publish(List<Event> events);
	
}
