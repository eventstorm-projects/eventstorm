package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.EventLoop;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractEventCommandHandler<T extends Command> implements CommandHandler<T, Event> {
	
	@Autowired
	private EventLoop eventLoop;
	
	public final Flux<Event> handle(CommandContext context) {
		
		return Mono.just(context)
			.map(ctx -> {
				// validate the command
				validate(ctx, ctx.getCommand());
				return context;
			})
			.flatMap(ctx ->
					// apply the decision function (state,command) => events
					Mono.just(decision(ctx, ctx.getCommand()))
						// publish on event lopp before store
						.publishOn(eventLoop.get(ctx.getCommand()))
			)
			// save the to the eventStore
			.map(this::store)
			// apply the evolution function (state,Event) => State
			.doOnSuccess(this::evolution)
			// publish events
			.doOnSuccess(this::publish)
			.flatMapMany(Flux::fromIterable);
		
	}

	
	protected abstract void validate(CommandContext context, T command);

	/**
	 * (state,command) => events
	 */
	protected abstract ImmutableList<EventCandidate<?>> decision(CommandContext context, T command);

	
	protected abstract ImmutableList<Event> store(ImmutableList<EventCandidate<?>> candidates);

	/**
	 *  (state,Event) => State
	 */
	protected abstract void evolution(ImmutableList<Event> events);
	
	/**
	 * publish events
	 */
	protected abstract void publish(ImmutableList<Event> events);

}