package eu.eventstorm.core.ex001.gen.domain;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventData;
import eu.eventstorm.core.EventListener;
import eu.eventstorm.core.ex001.event.UserCreatedEvent;
import eu.eventstorm.core.ex001.event.UserMailModifiedEvent;

public abstract class UserDomainHandler implements EventListener {

	abstract void onUserCreatedEvent(Event<UserCreatedEvent> usercreatedevent);

	abstract void onUserMailModifiedEvent(Event<UserMailModifiedEvent> userMailModifiedEvent);

	@SuppressWarnings("unchecked")
	@Override
	public void accept(Event<? extends EventData> event) {
		Class<? extends EventData> clazz = event.getEventData().getClass();

		if (UserCreatedEvent.class.isAssignableFrom(clazz)) {
			onUserCreatedEvent((Event<UserCreatedEvent>) event);
		}

		if (clazz.isAssignableFrom(UserMailModifiedEvent.class)) {
			onUserMailModifiedEvent((Event<UserMailModifiedEvent>) event);
		}

	}


}