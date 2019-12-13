package eu.eventstorm.core.eventbus;

import java.util.List;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBus;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DualEventBus implements EventBus {

	private final EventBus one;
	private final EventBus two;
	
	public DualEventBus(EventBus one, EventBus two) {
		super();
		this.one = one;
		this.two = two;
	}


	@Override
	public void publish(List<Event<?>> events) {
		try {
			one.publish(events);
		} finally {
			two.publish(events);
		}
	}

}