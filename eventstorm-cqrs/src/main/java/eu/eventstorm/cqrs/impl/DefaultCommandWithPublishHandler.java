package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.EventStoreClient;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultCommandWithPublishHandler<T extends Command> extends DefaultCommandHandler<T> {

	private final EventBus eventBus;
	
	public DefaultCommandWithPublishHandler(Class<T> type, Validator<T> validator, EventStoreClient eventStoreClient, 
			EvolutionHandlers evolutionHandlers, EventBus eventBus) {
		super(type, validator, eventStoreClient, evolutionHandlers);
		this.eventBus = eventBus;
	}

	@Override
	protected void publish(ImmutableList<Event> events) {
		this.eventBus.publish(events);
	}

}