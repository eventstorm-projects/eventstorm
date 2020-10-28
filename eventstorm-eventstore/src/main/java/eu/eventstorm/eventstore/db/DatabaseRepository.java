package eu.eventstorm.eventstore.db;

import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.STREAM;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.STREAM_ID;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.ALL;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.PAYLOAD;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.EVENT_TYPE;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.REVISION;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.TABLE;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.TIME;
import static eu.eventstorm.sql.builder.Order.asc;
import static eu.eventstorm.sql.builder.Order.desc;
import static eu.eventstorm.sql.expression.AggregateFunctions.max;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;

import java.util.stream.Stream;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.jdbc.ResultSetMappers;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseRepository extends AbstractDatabaseEventRepository {

	private final SqlQuery findByAggreateTypeAndAggregateIdLock;
	private final SqlQuery findByAggreateTypeAndAggregateId;
	private final SqlQuery lastRevision;

	DatabaseRepository(Database database) {
		super(database);
		this.findByAggreateTypeAndAggregateIdLock = select(ALL).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).orderBy(desc(REVISION)).forUpdate()
		        .build();
		this.lastRevision = select(max(REVISION)).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).build();
		
		this.findByAggreateTypeAndAggregateId = select(TIME, REVISION, PAYLOAD, EVENT_TYPE).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).orderBy(asc(REVISION))
		        .build();
	}

	public long lastRevision(String stream, String streamId) {
		return executeSelect(this.lastRevision, ps -> {
			ps.setString(1, stream);
			ps.setString(2, streamId);
		}, ResultSetMappers.LONG);
	}
	
	public Stream<DatabaseEvent> lock(String stream, String streamId) {
		return stream(this.findByAggreateTypeAndAggregateIdLock, ps -> {
			ps.setString(1, stream);
			ps.setString(2, streamId);
		}, Mappers.DATABASE_EVENT);
	}
	
//	public Stream<DatabaseEvent> lock(String stream, String streamId) {
//		return stream(this.findByAggreateTypeAndAggregateIdLock, ps -> {
//			ps.setString(1, stream);
//			ps.setString(2, streamId);
//		}, Mappers.DATABASE_EVENT);
//	}

	public  <T> Stream<T> findAllByStreamAndStreamId(String stream, String streamId, ResultSetMapper<T> rsm) {
		return stream(this.findByAggreateTypeAndAggregateId, ps -> {
			ps.setString(1, stream);
			ps.setString(2, streamId);
		}, rsm);
	}

}
