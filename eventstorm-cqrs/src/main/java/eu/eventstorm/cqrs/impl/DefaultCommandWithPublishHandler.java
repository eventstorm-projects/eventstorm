package eu.eventstorm.cqrs.impl;

import eu.eventstorm.cqrs.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.eventbus.EventBus;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultCommandWithPublishHandler<T extends Command> extends DefaultEventCommandHandler<T> {

	@Autowired
	private EventBus eventBus;
	
	protected DefaultCommandWithPublishHandler(Class<T> type, Validator<T> validator) {
		super(type, validator);
	}

	@Override
	protected void publish(ImmutableList<Event> events) {
		this.eventBus.publish(events);
	}

}