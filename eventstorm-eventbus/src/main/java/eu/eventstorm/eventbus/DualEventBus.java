package eu.eventstorm.eventbus;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

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
	public void publish(Event event) {
		try {
			one.publish(event);
		} finally {
			two.publish(event);
		}
	}


	@Override
	public void publish(ImmutableList<Event> events) {
		// TODO Auto-generated method stub
		
	}

}