package eu.eventstorm.core.eventstore;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.time.OffsetDateTime.ofInstant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.impl.Events;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.Transaction;
import eu.eventstorm.sql.impl.TransactionManager;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseEventStore implements EventStore {

	private final TransactionManager transactionManager;

	private final DatabaseRepository databaseRepository;

	public DatabaseEventStore(Database database) {
		this.transactionManager = database.transactionManager();
		this.databaseRepository = new DatabaseRepository(database);
	}

	@Override
	public Stream<Event> readStream(String stream, AggregateId aggregateId) {
		ImmutableList<Event> list;
		try (Transaction transaction = transactionManager.newTransactionReadOnly()) {
			list = this.databaseRepository.findAllByAggragateTypeAndAggregateId(stream, aggregateId.toStringValue(),
			        new EventResultSetMapper(aggregateId, stream)).collect(toImmutableList());
			transaction.rollback();
		}
		return list.stream();
	}

	@Override
	public Event appendToStream(String stream, AggregateId id, EventPayload payload) {
		OffsetDateTime time = OffsetDateTime.now();
		try (Transaction transaction = transactionManager.newTransactionReadWrite()) {

			Stream<DatabaseEvent> events = this.databaseRepository.lock(stream, id.toStringValue());
			Optional<DatabaseEvent> optional = events.findFirst();

			DatabaseEventBuilder builder = new DatabaseEventBuilder().aggregateId(id.toStringValue()).aggregateType(stream)
			        .time(Timestamp.from(time.toInstant()));

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

	private static final class EventResultSetMapper implements ResultSetMapper<Event> {
		private final AggregateId aggregateId;
		private final String aggregateType;

		private EventResultSetMapper(AggregateId aggregateId, String aggregateType) {
			this.aggregateId = aggregateId;
			this.aggregateType = aggregateType;
		}

		@Override
		public Event map(Dialect dialect, ResultSet rs) throws SQLException {
			return Events.newEvent(aggregateId, aggregateType, ofInstant(rs.getTimestamp(1).toInstant(), ZONE_ID), rs.getInt(2), null);
		}
	}
}