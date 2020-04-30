package eu.eventstorm.eventbus;

import java.util.function.Consumer;

import eu.eventstorm.core.Event;

public interface EventListener extends Consumer<Event> {

	
}