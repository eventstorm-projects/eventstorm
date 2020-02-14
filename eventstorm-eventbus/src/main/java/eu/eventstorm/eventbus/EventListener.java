package eu.eventstorm.eventbus;

import java.util.function.Consumer;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

public interface EventListener extends Consumer<Event<? extends EventPayload>> {

	
}