package eu.eventstorm.core.ex001.event;

import eu.eventstorm.core.EventData;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface UserCreatedEvent extends EventData {

	String getName();

	int getAge();

	String getEmail();

}
