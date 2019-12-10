package eu.eventstorm.core.eventstore;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.of;
import static java.time.OffsetDateTime.ofInstant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventPayloadSchema;
import eu.eventstorm.core.EventPayloadSchemaRegistry;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.impl.Events;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.Transaction;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseEventStore implements EventStore {

	private final Database database;
	
	private final DatabaseRepository databaseRepository;
	
	private final ObjectMapper mapper;
	
	private final EventPayloadSchemaRegistry schemaRegistry;

	public DatabaseEventStore(Database database, EventPayloadSchemaRegistry schemaRegistry) {
		this.database = database;
		this.databaseRepository = new DatabaseRepository(database);
		this.mapper = new ObjectMapper();
		this.schemaRegistry = schemaRegistry;
	}

	@Override
	public Stream<Event> readStream(String stream, AggregateId aggregateId) {
		ImmutableList<Event> list;
		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {
			list = this.databaseRepository.findAllByAggragateTypeAndAggregateId(stream, aggregateId.toStringValue(),
			        new EventResultSetMapper(aggregateId, stream)).collect(toImmutableList());
			transaction.rollback();
		}
		return list.stream();
	}

	@Override
	public Event appendToStream(String stream, AggregateId id, EventPayload payload) {
		OffsetDateTime time = OffsetDateTime.now();
		
		byte[] content;

		try {
			content = this.mapper.writeValueAsBytes(payload);
		} catch (JsonProcessingException cause) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, of("aggregateType", stream, "aggregateId", id, "payload", payload), cause);
		}
		
		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {

			Stream<DatabaseEvent> events = this.databaseRepository.lock(stream, id.toStringValue());
			Optional<DatabaseEvent> optional = events.findFirst();

			EventPayloadSchema schema = this.schemaRegistry.getSchema(payload);
			
			DatabaseEventBuilder builder = new DatabaseEventBuilder()
					.aggregateId(id.toStringValue())
					.aggregateType(stream)
			        .time(Timestamp.from(time.toInstant()))
			        .payload(database.dialect().createJson(content))
			        .payloadSchema(schema.getName())
			        .payloadSchemaVersion(schema.getVersion())
			        ;

			if (optional.isPresent()) {
				// "update"
				builder.revision(optional.get().getRevision() + 1);
			} else {
				// "insert"
				builder.revision(1);
			}

			this.databaseRepository.insert(builder.build());

			transaction.commit();
		}
		return Events.newEvent(id, stream, time, 0, payload);
	}

	private static final ZoneId ZONE_ID = ZoneId.of("UTC");

	private final class EventResultSetMapper implements ResultSetMapper<Event> {
		
		private final AggregateId aggregateId;
		private final String aggregateType;

		private EventResultSetMapper(AggregateId aggregateId, String aggregateType) {
			this.aggregateId = aggregateId;
			this.aggregateType = aggregateType;
		}

		@Override
		public Event map(Dialect dialect, ResultSet rs) throws SQLException {
			byte[] payload = rs.getBytes(3);
		//	EventPayload eventPayload = schemaRegistry.getDeserializer(rs.getString(4), rs.getInt(5)).deserialize(payload);
			return Events.newEvent(aggregateId, aggregateType, ofInstant(rs.getTimestamp(1).toInstant(), ZONE_ID), rs.getInt(2), null);
		}
	}
}