package eu.eventstorm.eventstore.db;

import com.google.protobuf.Message;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseEventStore extends LocalDatabaseEventStore {

	private final Database database;
	
	public DatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager) {
		this(database, eventStoreProperties, streamManager, JsonPayloadManager.INSTANCE);
	}

	public DatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager, PayloadManager payloadManager) {
		super(database, eventStoreProperties, streamManager);
		this.database = database;
	}

	@Override
	public <T extends Message> Event appendToStream(EventCandidate<T> candidate, String correlation) {
		Event event;
		try (Transaction transaction = database.transactionManager().newTransactionReadWrite()) {
			event = super.appendToStream(candidate, correlation);
			transaction.commit();
		}
		return event; 
	}

}