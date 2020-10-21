package eu.eventstorm.cqrs.impl;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.BatchCommand;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.util.tuple.Tuples;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractBatchEventCommandHandler<C extends BatchCommand> implements CommandHandler<C, Event> {

	private final Class<C> type;

	private final Batch batch;

	public AbstractBatchEventCommandHandler(Class<C> type, Batch batch) {
		this.type = type;
		this.batch = batch;
	}

	@Override
	public final Class<C> getType() {
		return type;
	}

	@Override
	public Flux<Event> handle(CommandContext context, C command) {
		
		return Mono.just(Tuples.of(context, command))
				// validate the command
				.doOnNext(t -> validate(t.getT1(), t.getT2()))
				// apply the decision function (state,command) => events
				.map(t -> decision(t.getT1(), t.getT2()))
				// push the candidate to the batch
				.map(candidate -> this.batch.push(candidate))
				// to flux
				.flux();
				
	}

	protected abstract EventCandidate<BatchJobCreated> decision(CommandContext context, C command);

	protected abstract void validate(CommandContext context, C command);

}
