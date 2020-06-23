package eu.eventstorm.eventstore.db;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.Statistics;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamEventDefinition;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.type.common.Blobs;
import eu.eventstorm.sql.util.TransactionStreamTemplate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseEventStore implements EventStore {

	private static final JsonFormat.Printer PRINTER = JsonFormat.printer().omittingInsignificantWhitespace();
	
	private final Database database;
	
	private final DatabaseRepository databaseRepository;
	
	private final TransactionStreamTemplate streamTemplate;
	
	public DatabaseEventStore(Database database) {
		this.database = database;
		this.databaseRepository = new DatabaseRepository(database);
		this.streamTemplate = new TransactionStreamTemplate(database.transactionManager());
	}

	@Override
	public Event appendToStream(StreamEventDefinition sepd, String streamId, java.util.UUID correlation, Message message) {
		
		OffsetDateTime time = OffsetDateTime.now();

		byte[] json;
		try {
			json = PRINTER.print(message).getBytes(StandardCharsets.UTF_8);
		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
			return null;
		}
		
		DatabaseEvent de;
		
		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {
			try (Stream<DatabaseEvent> events = this.databaseRepository.lock(sepd.getStream(), streamId)) {
				Optional<DatabaseEvent> optional = events.findFirst();
				DatabaseEventBuilder builder = new DatabaseEventBuilder()
							.withStreamId(streamId)
							.withStream(sepd.getStream())
					        .withTime(Timestamp.from(time.toInstant()))
					        .withPayload(Blobs.newBlob(json))
					        .withCorrelation(correlation.toString())
					        .withEventType(sepd.getEventType());

				if (optional.isPresent()) {
					// "update"
					builder.withRevision(optional.get().getRevision() + 1);
				} else {
					// "insert"
					builder.withRevision(1);
				}

				de = builder.build();
				
				this.databaseRepository.insert(de);
			}
			transaction.commit();
		}

		// @formatter:off
		return Event.newBuilder()
					.setStreamId(streamId)
					.setStream(sepd.getStream())
					.setTimestamp(time.toString())
					.setRevision(de.getRevision())
					.setData(Any.pack(message,"eventstorm"))
					.build();
		// @formatter:off
	}

	@Override
	public Stream<Event> readStream(StreamDefinition definition, String streamId) {
		return streamTemplate.stream(() -> 
		this.databaseRepository.findAllByStreamAndStreamId(definition.getName(), streamId,  new EventResultSetMapper(streamId, definition)));

	}

	@Override
	public Statistics stat(String stream) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public Stream<Event<EventPayload>> readStream(StreamDefinition definition, String streamId) {
//	}
//
//	//@Override
//	public <T extends AbstractMessage> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, T eventPayload) {
//		//return appendToStream(sepd, streamId, sepd.getPayloadSerializer().serialize(eventPayload));
//		return null;
//	}
//
//	@Override
//	public <T extends AbstractMessage> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, byte[] eventPayload) {
//	
//		OffsetDateTime time = OffsetDateTime.now();
//		DatabaseEvent de;
//		
//		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {
//			try (Stream<DatabaseEvent> events = this.databaseRepository.lock(sepd.getStream(), streamId)) {
//				Optional<DatabaseEvent> optional = events.findFirst();
//
//				DatabaseEventBuilder builder = new DatabaseEventBuilder()
//						.withStreamId(streamId)
//						.withStream(sepd.getStream())
//				        .withTime(Timestamp.from(time.toInstant()))
//				        .withPayload(Blobs.newBlob(eventPayload))
//				        //.withEventType(sepd.getPayloadType())
//				        ;
//
//				if (optional.isPresent()) {
//					// "update"
//					builder.withRevision(optional.get().getRevision() + 1);
//				} else {
//					// "insert"
//					builder.withRevision(1);
//				}
//
//				de = builder.build();
//				
//				this.databaseRepository.insert(de);
//			}
//			transaction.commit();
//		}
//		
//		// @formatter:off
////		return  new EventBuilder<T>()
////					.withStreamId(StreamIds.from(streamId))
////					.withStream(sepd.getStream())
////					.withTimestamp(time)
////					.withRevision(de.getRevision())
////					.withPayload(null)
////					.build();
//		// @formatter:on
//	}
//	
////	@Override
////	public <T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload) {
////		byte[] content;
////
////		try {
////			content = this.mapper.writeValueAsBytes(payload);
////		} catch (JsonProcessingException cause) {
////			throw new EventStoreException(EventStoreException.Type.FAILED_TO_SERILIAZE_PAYLOAD, of("aggregateType", aggregateType, "aggregateId", id, "payload", payload), cause);
////		}
////		
////		return appendToStream(aggregateType, id, payload, content);
////		
////		
////	}
//
	private static final ZoneId ZONE_ID = ZoneId.of("UTC");

	private final class EventResultSetMapper implements ResultSetMapper<Event> {
		
		private final StreamDefinition definition;
		private final String streamId;

		private EventResultSetMapper(String streamId, StreamDefinition definition) {
			this.streamId = streamId;
			this.definition = definition;
		}

		@Override
		public Event map(Dialect dialect, ResultSet rs) throws SQLException {
			Message message = definition.getStreamEventDefinition(rs.getString(4)).jsonParse(rs.getBytes(3));
			// @formatter:off
			Event event = Event.newBuilder()
					.setStreamId(streamId)
					.setStream(definition.getName())
					//.setTimestamp(OffsetDateTime.ofInstant(rs.getTimestamp(DatabaseEventDescriptor.INDEX__TIME).toInstant(), ZONE_ID).toString())
					.setRevision(rs.getInt(2))
					.setData(Any.pack(message,"event"))
					.build();
			// @formatter:on
			return event;
		}
	}
//
//	@Override
//	public Statistics stat(String stream) {
//		return null;
//	}

}