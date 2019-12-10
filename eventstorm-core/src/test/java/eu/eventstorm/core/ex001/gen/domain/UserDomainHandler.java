package eu.eventstorm.core.ex001.gen.domain;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventListener;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.core.ex001.event.UserMailModifiedEvent;

public abstract class UserDomainHandler implements EventListener {

	abstract void onUserCreatedEvent(Event usercreatedevent);

	abstract void onUserMailModifiedEvent(Event userMailModifiedEvent);

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Event event) {
		Class<? extends EventPayload> clazz = event.data().getClass();

		if (UserCreatedEventPayload.class.isAssignableFrom(clazz)) {
			onUserCreatedEvent(event);
		}

		if (clazz.isAssignableFrom(UserMailModifiedEvent.class)) {
			onUserMailModifiedEvent(event);
		}

	}


}