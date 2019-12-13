package eu.eventstorm.core.ex001.gen.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.core.ex001.event.UserMailModifiedEvent;

public final class UserDomainHandlerImpl extends UserDomainHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDomainHandlerImpl.class);
	
	@Override
	void onUserCreatedEvent(Event<UserCreatedEventPayload> usercreatedevent) {
		LOGGER.debug("onUserCreatedEvent->({}]", usercreatedevent);
	}

	@Override
	void onUserMailModifiedEvent(Event<UserMailModifiedEvent> userMailModifiedEvent) {
		LOGGER.debug("onUserMailModifiedEvent->({}]", userMailModifiedEvent);
	}

}
