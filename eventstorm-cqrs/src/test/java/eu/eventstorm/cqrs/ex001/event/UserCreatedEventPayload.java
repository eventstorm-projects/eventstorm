package eu.eventstorm.cqrs.ex001.event;

import eu.eventstorm.annotation.CqrsEventPayload;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.cqrs.ex001.domain.User;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@CqrsEventPayload(domain = User.class)
public interface UserCreatedEventPayload extends EventPayload {

	String getName();

	int getAge();

	String getEmail();

}
