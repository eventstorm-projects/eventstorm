package eu.eventstorm.core.eventstore;

import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.AGGREGATE_ID;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.AGGREGATE_TYPE;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.ALL;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.TABLE;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.REVISION;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.PAYLOAD;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.PAYLOAD_TYPE;
import static eu.eventstorm.core.eventstore.DatabaseEventDescriptor.TIME;
import static eu.eventstorm.sql.builder.Order.desc;
import static eu.eventstorm.sql.builder.Order.asc;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;

import java.util.stream.Stream;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseRepository extends AbstractDatabaseEventRepository {

	private final String findByAggreateTypeAndAggregateIdLock;
	private final String findByAggreateTypeAndAggregateId;

	DatabaseRepository(Database database) {
		super(database);
		this.findByAggreateTypeAndAggregateIdLock = select(ALL).from(TABLE).where(and(eq(AGGREGATE_TYPE), eq(AGGREGATE_ID))).orderBy(desc(REVISION)).forUpdate()
		        .build();
		this.findByAggreateTypeAndAggregateId = select(TIME, REVISION, PAYLOAD, PAYLOAD_TYPE).from(TABLE).where(and(eq(AGGREGATE_TYPE), eq(AGGREGATE_ID))).orderBy(asc(REVISION))
		        .build();
	}

	public Stream<DatabaseEvent> lock(String aggregateType, String aggregateId) {
		return stream(this.findByAggreateTypeAndAggregateIdLock, ps -> {
			ps.setString(1, aggregateType);
			ps.setString(2, aggregateId);
		}, Mappers.DATABASE_EVENT);
	}

	public <T> Stream<T> findAllByAggragateTypeAndAggregateId(String aggregateType, String aggregateId, ResultSetMapper<T> rsm) {
		return stream(this.findByAggreateTypeAndAggregateId, ps -> {
			ps.setString(1, aggregateType);
			ps.setString(2, aggregateId);
		}, rsm);
	}

}
