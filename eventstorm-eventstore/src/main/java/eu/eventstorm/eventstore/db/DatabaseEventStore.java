package eu.eventstorm.eventstore.db;

import com.google.protobuf.Message;

import eu.eventstorm.core.Event;
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
		super(database, eventStoreProperties, streamManager);
		this.database = database;
	}

	@Override
	public Event appendToStream(String stream, String streamId, String correlation, Message message) {
		Event event;
		try (Transaction transaction = database.transactionManager().newTransactionReadWrite()) {
			event = super.appendToStream(stream, streamId, correlation, message);
			transaction.commit();
		};
		return event; 
	}

}