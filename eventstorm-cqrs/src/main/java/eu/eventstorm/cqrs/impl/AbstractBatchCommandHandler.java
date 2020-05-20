package eu.eventstorm.cqrs.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.BatchCommand;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

public abstract class AbstractBatchCommandHandler<C extends BatchCommand> implements CommandHandler<C> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBatchCommandHandler.class);

	private final Class<C> type;

	private final Batch batch;

	public AbstractBatchCommandHandler(Class<C> type, Batch batch) {
		this.type = type;
		this.batch = batch;
	}

	@Override
	public final Class<C> getType() {
		return type;
	}

	@Override
	public ImmutableList<Event> handle(C command) {
		
		// validate the command
		validate(command);

		// apply the decision function (state,command) => events
		EventCandidate<BatchJobCreated> data = decision(command);

		// push the candidate to the batch
		Event event = this.batch.push(data);
		
		return ImmutableList.of(event);
	}

	protected abstract EventCandidate<BatchJobCreated> decision(C command);

	protected abstract void validate(C command);

}
