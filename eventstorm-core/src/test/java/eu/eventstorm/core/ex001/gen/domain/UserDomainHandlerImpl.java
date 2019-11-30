package eu.eventstorm.core.ex001.gen.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;

public final class UserDomainHandlerImpl extends UserDomainHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDomainHandlerImpl.class);
	
	@Override
	void onUserCreatedEvent(Event usercreatedevent) {
		LOGGER.debug("onUserCreatedEvent->({}]", usercreatedevent);
	}

	@Override
	void onUserMailModifiedEvent(Event userMailModifiedEvent) {
		LOGGER.debug("onUserMailModifiedEvent->({}]", userMailModifiedEvent);
	}

}
