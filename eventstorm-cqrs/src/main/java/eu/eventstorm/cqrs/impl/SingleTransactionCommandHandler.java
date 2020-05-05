package eu.eventstorm.cqrs.impl;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandException;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.EventCandidate;
import eu.eventstorm.eventstore.EventStoreClient;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.TransactionManager;
import reactor.core.publisher.Flux;

public abstract class SingleTransactionCommandHandler<T extends Command> extends AbstractCommandHandler<T> {
	
	private final TransactionManager transactionManager;
	
	public SingleTransactionCommandHandler(Class<T> type, Validator<T> validator, EventStoreClient eventStoreClient, EventBus eventBus, TransactionManager transactionManager) {
		super(type, validator, eventStoreClient, eventBus);
		this.transactionManager = transactionManager;
	}
	
	
	@Override
	public final Flux<Event> handle(T command) {
		
		// validate the command
		validate(command);
		
		ImmutableList<Event> events = null;
		
		try (Transaction tx = transactionManager.newTransactionReadWrite()) {
			// apply the decision function (state,command) => events
			ImmutableList<EventCandidate> candidates = decision(command);
			
			// save the to the eventStore
			events = getEventStoreClient().appendToStream(candidates).collect(toImmutableList());
			
			// apply the evolution function (state,Event) => State
			evolution(events);
			
			tx.commit();
		} catch (Exception cause) {

			// check if command exception => throw as is
			if (cause instanceof CommandException) {
				throw cause;
			}
			
			if (events != null) {
				compensating(events, cause);
			}
			
			throw new SingleTransactionCommandHandlerException(command, "failed to handle the command in a singleTransaction", cause, ImmutableMap.of("event", events));
		}
		
		return Flux.fromIterable(events);
	}

	protected abstract void compensating(ImmutableList<Event> events, Exception cause);

}
