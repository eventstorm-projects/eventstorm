package eu.eventstorm.eventstore.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBuilder;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.id.StreamIds;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamEvantPayloadDefinition;
import eu.eventstorm.eventstore.StreamManager;
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

	private final Database database;
	
	private final DatabaseRepository databaseRepository;
	
	private final ObjectMapper mapper;
	
	private final TransactionStreamTemplate streamTemplate;
	
	private final StreamManager streamManager;
	
	public DatabaseEventStore(Database database, StreamManager streamManager) {
		this.database = database;
		this.streamManager = streamManager;
		this.databaseRepository = new DatabaseRepository(database);
		this.mapper = new ObjectMapper();
		this.streamTemplate = new TransactionStreamTemplate(database);
	}

	@Override
	public Stream<Event<?>> readStream(StreamDefinition definition, String streamId) {
		return streamTemplate.decorate(() -> 
			this.databaseRepository.findAllByAggragateTypeAndAggregateId(definition.getName(), streamId,  new EventResultSetMapper(definition)));
	}

	@Override
	public <T extends EventPayload> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, T eventPayload) {
		return appendToStream(sepd, streamId, sepd.getPayloadSerializer().serialize(eventPayload));
	}

	@Override
	public <T extends EventPayload> Event<T> appendToStream(StreamEvantPayloadDefinition<T> sepd, String streamId, byte[] eventPayload) {
	
		OffsetDateTime time = OffsetDateTime.now();
		DatabaseEvent de;
		
		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {
			try (Stream<DatabaseEvent> events = this.databaseRepository.lock(sepd.getStream(), streamId)) {
				Optional<DatabaseEvent> optional = events.findFirst();

				DatabaseEventBuilder builder = new DatabaseEventBuilder()
						.withAggregateId(streamId)
						.withAggregateType(sepd.getStream())
				        .withTime(Timestamp.from(time.toInstant()))
				        .withPayload(Blobs.newBlob(eventPayload))
				        .withPayloadType(sepd.getPayloadType())
				        ;

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
		return  new EventBuilder<T>()
					.withStreamId(StreamIds.from(streamId))
					.withStream(sepd.getStream())
					.withTimestamp(time)
					.withRevision(de.getRevision())
					.withPayload(null)
					.build();
		// @formatter:on
	}
	
//	@Override
//	public <T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload) {
//		byte[] content;
//
//		try {
//			content = this.mapper.writeValueAsBytes(payload);
//		} catch (JsonProcessingException cause) {
//			throw new EventStoreException(EventStoreException.Type.FAILED_TO_SERILIAZE_PAYLOAD, of("aggregateType", aggregateType, "aggregateId", id, "payload", payload), cause);
//		}
//		
//		return appendToStream(aggregateType, id, payload, content);
//		
//		
//	}

	private static final ZoneId ZONE_ID = ZoneId.of("UTC");

	private final class EventResultSetMapper implements ResultSetMapper<Event<?>> {
		
		private final StreamDefinition definition;

		private EventResultSetMapper(StreamDefinition definition) {
			this.definition = definition;
		}

		@Override
		public Event<EventPayload> map(Dialect dialect, ResultSet rs) throws SQLException {
			byte[] payload = rs.getBytes(3);
			String payloadType = rs.getString(4);
			EventPayload eventPayload = definition.getStreamEvantPayloadDefinition(payloadType).getPayloadDeserializer().deserialize(payload);
			// @formatter:off
			return new EventBuilder<EventPayload>()
						.withStreamId(StreamIds.from(""))
						.withStream(definition.getName())
						.withTimestamp(OffsetDateTime.ofInstant(rs.getTimestamp(1).toInstant(), ZONE_ID))
						.withRevision(rs.getInt(2))
						.withPayload(eventPayload)
						.build();
			// @formatter:on
		}
	}

}