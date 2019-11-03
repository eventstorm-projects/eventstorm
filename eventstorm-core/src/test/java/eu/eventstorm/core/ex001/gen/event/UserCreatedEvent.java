package eu.eventstorm.core.ex001.gen.event;


import java.time.OffsetDateTime;

import eu.eventstorm.core.Event;

// Created + UserCommand + Event
public final class UserCreatedEvent extends Event {

	public UserCreatedEvent(String id, OffsetDateTime timestamp, String type, String contentType) {
		super(id, timestamp, type, contentType);
	}

    

}
