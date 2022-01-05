package eu.eventstorm.batch.cqrs;

import eu.eventstorm.batch.Batch;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.BatchCommand;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractBatchEventCommandHandler<C extends BatchCommand> implements CommandHandler<C, Event> {

	private final Class<C> type;

	private final Batch batch;

	protected AbstractBatchEventCommandHandler(Class<C> type, Batch batch) {
		this.type = type;
		this.batch = batch;
	}

	@Override
	public final Class<C> getType() {
		return type;
	}

	@Override
	public Flux<Event> handle(CommandContext context) {
		
		return Mono.just(context)
				// validate the command
				.doOnNext(this::validate)
				// apply the decision function (state,command) => events
				.map(this::decision)
				// push the candidate to the batch
				.map(this.batch::push)
				// to flux
				.flux();
				
	}

	protected abstract EventCandidate<BatchJobCreated> decision(CommandContext context);

	protected abstract void validate(CommandContext context);

}
