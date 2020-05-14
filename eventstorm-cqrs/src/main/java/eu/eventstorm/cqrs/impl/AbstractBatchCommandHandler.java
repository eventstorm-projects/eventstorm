package eu.eventstorm.cqrs.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.BatchCommand;

public abstract class AbstractBatchCommandHandler<C extends BatchCommand> extends AbstractCommandHandler<C> {

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
	protected final ImmutableList<Event> store(ImmutableList<EventCandidate> candidates) {
		return batch.push(candidates);
	}

	@Override
	protected final void evolution(ImmutableList<Event> events) {
		// nothing to do ...
		
	}

	@Override
	protected void publish(ImmutableList<Event> events) {
		// nothing to do ...
	}
	
	

//	@Override
//	public ImmutableList<Event> handle(C command) {
//		
//		// validate the command
//		validate(command);
//		
//		
//		
//		
////		BatchExecution batchExecution = registerBatch(command);
////		
////		Event event = Event.newBuilder()
////				.setStreamId(String.valueOf(batchExecution.getId()))
////				.setStream("batch")
////				//.setCorrelation(UUID.newBuilder().batchExecution.getUuid())
////				.setRevision(1)
////				.setTimestamp(batchExecution.getCreatedAt().toString())
////				.build();
////		
//		
//		
////		return ImmutableList.of(event);
//		
//		return null;
//	}

//	private BatchExecution registerBatch(C command) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	protected abstract void validate(C command);

}
	