package eu.eventstorm.core.ex001.gen.domain;

import eu.eventstorm.core.DomainModel;
import eu.eventstorm.core.ex001.event.UserCreatedEvent;
import eu.eventstorm.core.ex001.event.UserMailModifiedEvent;

public interface UserDomainHandler extends DomainModel {

	void apply(UserCreatedEvent userCreatedEvent);

	void apply(UserMailModifiedEvent userMailModifiedEvent);
	
}