package eu.eventstorm.cqrs.ex001.handler;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.ex001.command.UpdateUserMailCommand;
import eu.eventstorm.cqrs.impl.DefaultCommandHandler;
import eu.eventstorm.cqrs.validation.Validators;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.EventCandidate;
import eu.eventstorm.eventstore.EventStoreClient;

public class UpdateUserMailCommandHandler extends DefaultCommandHandler<UpdateUserMailCommand> {

	public UpdateUserMailCommandHandler(EventStoreClient eventStore, EvolutionHandlers evolutionHandlers, EventBus eventBus) {
		super(UpdateUserMailCommand.class, Validators.empty(), eventStore, evolutionHandlers,  eventBus);
	}

	@Override
	protected ImmutableList<EventCandidate> decision(UpdateUserMailCommand command) {
		return ImmutableList.of();
	}

}