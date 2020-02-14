package eu.eventstorm.eventstore.ex;

import eu.eventstorm.annotation.CqrsEventPayload;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@CqrsEventPayload(domain = UserDomainModel.class)
public interface UserCreatedEventPayload extends EventPayload {

	String getName();

	int getAge();

	String getEmail();

}
