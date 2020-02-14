package eu.eventstorm.cqrs.ex001.gen.domain;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.event.UserMailModifiedEvent;
import eu.eventstorm.eventbus.EventListener;

public abstract class UserDomainHandler implements EventListener {

	abstract void onUserCreatedEvent(Event<UserCreatedEventPayload> usercreatedevent);

	abstract void onUserMailModifiedEvent(Event<UserMailModifiedEvent> userMailModifiedEvent);

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Event<? extends EventPayload> event) {
		Class<? extends EventPayload> clazz = event.getPayload().getClass();

		if (UserCreatedEventPayload.class.isAssignableFrom(clazz)) {
			onUserCreatedEvent((Event<UserCreatedEventPayload>) event);
		}

		if (clazz.isAssignableFrom(UserMailModifiedEvent.class)) {
			onUserMailModifiedEvent((Event<UserMailModifiedEvent>) event);
		}

	}


}