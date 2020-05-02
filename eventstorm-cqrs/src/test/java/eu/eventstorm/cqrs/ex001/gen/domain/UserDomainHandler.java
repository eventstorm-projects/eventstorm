package eu.eventstorm.cqrs.ex001.gen.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventbus.EventListener;

public abstract class UserDomainHandler implements EventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDomainHandler.class);

	
	abstract void onUserCreatedEvent(Event usercreatedevent);

	abstract void onUserMailModifiedEvent(Event userMailModifiedEvent);

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Event event) {

		LOGGER.info("accept({})", event.getData().getTypeUrl());
		
		
//		if (UserCreatedEventPayload.class.isAssignableFrom(clazz)) {
//			onUserCreatedEvent((Event<UserCreatedEventPayload>) event);
//		}
//
//		if (clazz.isAssignableFrom(UserMailModifiedEvent.class)) {
//			onUserMailModifiedEvent((Event<UserMailModifiedEvent>) event);
//		}

	}


}