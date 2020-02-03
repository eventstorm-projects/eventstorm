package eu.eventstorm.core;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventBus {

	void publish(Event<EventPayload> event);
	
}
