package eu.eventstorm.core.eventstore;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.of;

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
import eu.eventstorm.core.EventPayloadRegistry;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.impl.EventBuilder;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.Transaction;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.type.common.Blobs;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseEventStore implements EventStore {

	private final Database database;
	
	private final DatabaseRepository databaseRepository;
	
	private final ObjectMapper mapper;
	
	private final EventPayloadRegistry registry;

	public DatabaseEventStore(Database database, EventPayloadRegistry registry) {
		this.database = database;
		this.databaseRepository = new DatabaseRepository(database);
		this.mapper = new ObjectMapper();
		this.registry = registry;
	}

	@Override
	public Stream<Event<? extends EventPayload>> readStream(String aggregateType, AggregateId aggregateId) {
		ImmutableList<Event<?>> list;
		try (Transaction transaction = database.transactionManager().newTransactionReadWrite()) {
			list = this.databaseRepository.findAllByAggragateTypeAndAggregateId(aggregateType, aggregateId.toStringValue(),
			        new EventResultSetMapper(aggregateId, aggregateType)).collect(toImmutableList());
			transaction.rollback();
		}
		return list.stream();
	}

	@Override
	public <T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload) {
		OffsetDateTime time = OffsetDateTime.now();
		
		byte[] content;

		try {
			content = this.mapper.writeValueAsBytes(payload);
		} catch (JsonProcessingException cause) {
			throw new EventStoreException(EventStoreException.Type.STREAM_NOT_FOUND, of("aggregateType", aggregateType, "aggregateId", id, "payload", payload), cause);
		}
		
		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {

			Stream<DatabaseEvent> events = this.databaseRepository.lock(aggregateType, id.toStringValue());
			Optional<DatabaseEvent> optional = events.findFirst();

			DatabaseEventBuilder builder = new DatabaseEventBuilder()
					.aggregateId(id.toStringValue())
					.aggregateType(aggregateType)
			        .time(Timestamp.from(time.toInstant()))
			        .payload(Blobs.newBlob(content))
			        .payloadType(registry.getPayloadType(payload))
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
		
		// @formatter:off
	/*	return  new EventBuilder()
					.aggregateId(id)
					.aggreateType(stream)
					.timestamp(time)
					.version(1)
					.payload(payload)
					.build();
					*/
		return null;
		// @formatter:on
	}

	private static final ZoneId ZONE_ID = ZoneId.of("UTC");

	private final class EventResultSetMapper implements ResultSetMapper<Event<?>> {
		
		private final AggregateId aggregateId;
		private final String aggregateType;

		private EventResultSetMapper(AggregateId aggregateId, String aggregateType) {
			this.aggregateId = aggregateId;
			this.aggregateType = aggregateType;
		}

		@Override
		public Event<?> map(Dialect dialect, ResultSet rs) throws SQLException {
			byte[] payload = rs.getBytes(3);
			EventPayload eventPayload = registry.getDeserializer(rs.getString(4)).deserialize(payload);
			// @formatter:off
			return new EventBuilder<EventPayload>()
						.aggregateId(aggregateId)
						.aggreateType(aggregateType)
						.timestamp(OffsetDateTime.ofInstant(rs.getTimestamp(1).toInstant(), ZONE_ID))
						.revision(rs.getInt(2))
						.payload(eventPayload)
						.build();
			// @formatter:on
		}
	}
}