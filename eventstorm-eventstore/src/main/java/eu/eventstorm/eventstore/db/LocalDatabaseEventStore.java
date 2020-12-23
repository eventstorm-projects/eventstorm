package eu.eventstorm.eventstore.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.*;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.util.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class LocalDatabaseEventStore implements EventStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDatabaseEventStore.class);

	private static final JsonFormat.Printer PRINTER = JsonFormat.printer().omittingInsignificantWhitespace().includingDefaultValueFields();
	
	private final EventStoreProperties eventStoreProperties;
	
	private final DatabaseRepository databaseRepository;
	
	private final TransactionTemplate template;
	
	private final StreamManager streamManager;
	
	public LocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager) {
		this.eventStoreProperties = eventStoreProperties;
		this.databaseRepository = new DatabaseRepository(database);
		this.template = new TransactionTemplate(database.transactionManager());
		this.streamManager = streamManager;
	}

	@Override
	public Event appendToStream(String stream, String streamId, String correlation, Message message) {
		
		OffsetDateTime time = OffsetDateTime.now();

		String json;
		try {
			json = PRINTER.print(message);
		} catch (InvalidProtocolBufferException cause) {
			throw new EventStoreException(EventStoreException.Type.FAILED_TO_SERIALIZE, ImmutableMap.of("stream", stream,
					"streamId", streamId, "message", message), cause);
		}
		
		int revision = this.databaseRepository.lastRevision(stream, streamId);

		DatabaseEventBuilder builder = new DatabaseEventBuilder()
						.withStreamId(streamId)
						.withStream(stream)
				        .withTime(Timestamp.from(time.toInstant()))
				        .withPayload(json)
				        .withRevision(revision + 1)
				        .withEventType(message.getClass().getSimpleName());
				        
		if (correlation != null) {
			builder.withCorrelation(correlation);
		}
			
		this.databaseRepository.insert(builder.build());

		// @formatter:off
		return Event.newBuilder()
					.setStreamId(streamId)
					.setStream(stream)
					.setTimestamp(time.toString())
					.setRevision(revision + 1)
					.setData(Any.pack(message,this.eventStoreProperties.getEventDataTypeUrl() + "/" + stream + "/"))
					.build();
		// @formatter:off
	}

	@Override
	public Stream<Event> readStream(String stream, String streamId) {
		StreamDefinition definition = streamManager.getDefinition(stream);
		return template.stream(() -> 
		this.databaseRepository.findAllByStreamAndStreamId(stream, streamId,  new EventResultSetMapper(streamId, definition)));

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
			Message message = definition.getStreamEventDefinition(rs.getString(4)).jsonParse(rs.getString(3));
			// @formatter:off
			return Event.newBuilder()
					.setStreamId(streamId)
					.setStream(definition.getName())
					//.setTimestamp(OffsetDateTime.ofInstant(rs.getTimestamp(DatabaseEventDescriptor.INDEX__TIME).toInstant(), ZONE_ID).toString())
					.setRevision(rs.getInt(2))
					.setData(Any.pack(message,"event"))
					.build();
			// @formatter:on
		}
	}
//
//	@Override
//	public Statistics stat(String stream) {
//		return null;
//	}

}