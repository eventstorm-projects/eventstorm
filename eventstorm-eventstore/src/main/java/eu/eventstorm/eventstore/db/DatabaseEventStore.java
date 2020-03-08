package eu.eventstorm.eventstore.db;

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

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBuilder;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.eventstore.EventPayloadRegistry;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreException;
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
	
	private final EventPayloadRegistry registry;
	
	private final TransactionStreamTemplate streamTemplate;

	public DatabaseEventStore(Database database, EventPayloadRegistry registry) {
		this.database = database;
		this.databaseRepository = new DatabaseRepository(database);
		this.mapper = new ObjectMapper();
		this.registry = registry;
		this.streamTemplate = new TransactionStreamTemplate(database);
	}

	@Override
	public Stream<Event<EventPayload>> readStream(String aggregateType, AggregateId aggregateId) {
		return streamTemplate.decorate(() -> 
			this.databaseRepository.findAllByAggragateTypeAndAggregateId(aggregateType, aggregateId.toStringValue(),
			        new EventResultSetMapper(aggregateId, aggregateType)));
	}

	@Override
	public <T extends EventPayload> Event<T> appendToStream(String aggregateType, AggregateId id, T payload) {
		OffsetDateTime time = OffsetDateTime.now();
		
		byte[] content;

		try {
			content = this.mapper.writeValueAsBytes(payload);
		} catch (JsonProcessingException cause) {
			throw new EventStoreException(EventStoreException.Type.FAILED_TO_SERILIAZE_PAYLOAD, of("aggregateType", aggregateType, "aggregateId", id, "payload", payload), cause);
		}
		
		DatabaseEvent de;
		
		try (Transaction transaction = database.transactionManager().newTransactionIsolatedReadWrite()) {
			try (Stream<DatabaseEvent> events = this.databaseRepository.lock(aggregateType, id.toStringValue())) {
				Optional<DatabaseEvent> optional = events.findFirst();

				DatabaseEventBuilder builder = new DatabaseEventBuilder()
						.withAggregateId(id.toStringValue())
						.withAggregateType(aggregateType)
				        .withTime(Timestamp.from(time.toInstant()))
				        .withPayload(Blobs.newBlob(content))
				        .withPayloadType(registry.getPayloadType(payload))
				        .withPayloadVersion(registry.getPayloadVersion(payload))
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
					.withAggregateId(id)
					.withAggreateType(aggregateType)
					.withTimestamp(time)
					.withRevision(de.getRevision())
					.withPayload(payload)
					.build();
		// @formatter:on
	}

	private static final ZoneId ZONE_ID = ZoneId.of("UTC");

	private final class EventResultSetMapper implements ResultSetMapper<Event<EventPayload>> {
		
		private final AggregateId aggregateId;
		private final String aggregateType;

		private EventResultSetMapper(AggregateId aggregateId, String aggregateType) {
			this.aggregateId = aggregateId;
			this.aggregateType = aggregateType;
		}

		@Override
		public Event<EventPayload> map(Dialect dialect, ResultSet rs) throws SQLException {
			byte[] payload = rs.getBytes(3);
			EventPayload eventPayload = registry.getDeserializer(rs.getString(4)).deserialize(payload);
			// @formatter:off
			return new EventBuilder<EventPayload>()
						.withAggregateId(aggregateId)
						.withAggreateType(aggregateType)
						.withTimestamp(OffsetDateTime.ofInstant(rs.getTimestamp(1).toInstant(), ZONE_ID))
						.withRevision(rs.getInt(2))
						.withPayload(eventPayload)
						.build();
			// @formatter:on
		}
	}
}