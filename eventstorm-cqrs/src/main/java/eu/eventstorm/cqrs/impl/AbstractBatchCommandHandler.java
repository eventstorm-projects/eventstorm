package eu.eventstorm.cqrs.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.BatchCommand;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.batch.BatchExecution;

public abstract class AbstractBatchCommandHandler<C extends BatchCommand> implements CommandHandler<C> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBatchCommandHandler.class);

	private final Class<C> type;

	public AbstractBatchCommandHandler(Class<C> type) {
		this.type = type;
	}

	@Override
	public final Class<C> getType() {
		return type;
	}

	@Override
	public ImmutableList<Event> handle(C command) {
		
		validate(command);
		
		BatchExecution batchExecution = registerBatch(command);
		
		Event event = Event.newBuilder()
				.setStreamId(String.valueOf(batchExecution.getId()))
				.setStream("batch")
				//.setCorrelation(UUID.newBuilder().batchExecution.getUuid())
				.setRevision(1)
				.setTimestamp(batchExecution.getCreatedAt().toString())
				.build();
		
		return ImmutableList.of(event);
	}

	private BatchExecution registerBatch(C command) {
		// TODO Auto-generated method stub
		return null;
	}

	protected abstract void validate(C command);

}
	