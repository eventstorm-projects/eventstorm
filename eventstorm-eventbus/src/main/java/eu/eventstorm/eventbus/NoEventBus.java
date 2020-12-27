package eu.eventstorm.eventbus;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class NoEventBus implements EventBus {

	@Override
	public void publish(ImmutableList<Event> events) {
		// empty => skip.
	}

}
