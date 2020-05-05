package eu.eventstorm.eventbus;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventBus {

	void publish(Event event);

	void publish(ImmutableList<Event> events);
	
}
