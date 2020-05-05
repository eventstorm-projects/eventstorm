package eu.eventstorm.cqrs.impl;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.EventCandidate;
import eu.eventstorm.eventstore.EventStoreClient;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultCommandHandler<T extends Command> extends AbstractCommandHandler<T> {
	
	public DefaultCommandHandler(Class<T> type, Validator<T> validator, EventStoreClient eventStoreClient, EventBus eventBus) {
		super(type, validator, eventStoreClient, eventBus);
	}
	
	public final Flux<Event> handle(T command) {
		
		// validate the command
		validate(command);
		
		// apply the decision function (state,command) => events
		ImmutableList<EventCandidate> candidates = decision(command);
		
		// save the to the eventStore
		ImmutableList<Event> events = getEventStoreClient().appendToStream(candidates).collect(toImmutableList());
		
		// apply the evolution function (state,Event) => State
		evolution(events);
		
		getEventBus().publish(events);
		
		return Flux.fromIterable(events);
	}

	
	

}