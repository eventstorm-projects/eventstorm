package eu.eventstorm.cqrs.ex001.handler;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.ex001.command.UpdateUserMailCommand;
import eu.eventstorm.cqrs.impl.DefaultEventCommandHandler;
import eu.eventstorm.cqrs.validation.Validators;
import eu.eventstorm.eventstore.EventStoreClient;

@Component
public class UpdateUserMailCommandHandler extends DefaultEventCommandHandler<UpdateUserMailCommand> {

	public UpdateUserMailCommandHandler(EventStoreClient eventStore, EvolutionHandlers evolutionHandlers) {
		super(UpdateUserMailCommand.class, Validators.empty());
	}

	@Override
	protected ImmutableList<EventCandidate<?>> decision(CommandContext context, UpdateUserMailCommand command) {
		return ImmutableList.of();
	}

}