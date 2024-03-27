package eu.eventstorm.eventstore.db;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.COLUMNS;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.EVENT_TYPE;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.PAYLOAD;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.REVISION;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.STREAM;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.STREAM_ID;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.TABLE;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.TIME;
import static eu.eventstorm.sql.builder.Order.asc;
import static eu.eventstorm.sql.expression.AggregateFunctions.coalesce;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.expression.MathematicalFunctions.add;

import java.util.stream.Stream;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.builder.SubSelects;
import eu.eventstorm.sql.desc.InsertableSqlPrimaryKey;
import eu.eventstorm.sql.jdbc.InsertMapper;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.jdbc.ResultSetMappers;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseRepository extends AbstractDatabaseEventRepository {

	private final SqlQuery findLastRevisionByAggregateTypeAndAggregateId;
	
	private final SqlQuery findByAggregateTypeAndAggregateId;

	private final SqlQuery optimizeInsert;

	DatabaseRepository(Database database) {
		super(database);
		this.optimizeInsert = insert(of(
						new InsertableSqlPrimaryKey(STREAM),
						new InsertableSqlPrimaryKey(STREAM_ID),
						new InsertableSqlPrimaryKey(REVISION, coalesce(SubSelects.from(select(add(REVISION,1)).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).orderBy(Order.desc(REVISION)).limit(1).build()), 1))),
					COLUMNS, TABLE)
				.returning(REVISION)
				.build();

		this.findLastRevisionByAggregateTypeAndAggregateId = select(REVISION).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).orderBy(Order.desc(REVISION)).limit(1).build();
		
		this.findByAggregateTypeAndAggregateId = select(TIME, REVISION, PAYLOAD, EVENT_TYPE).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).orderBy(asc(REVISION))
		        .build();
	}

	public Integer optimizeInsert(DatabaseEvent pojo) {
		return executeInsertWithReturning(this.optimizeInsert, OPTIMIZE_INSERT_MAPPER, pojo, ResultSetMappers.SINGLE_INTEGER);
	}

	public Integer lastRevision(String stream, String streamId) {
		return executeSelect(this.findLastRevisionByAggregateTypeAndAggregateId, ps -> {
			ps.setString(1, stream);
			dialect().setPreparedStatement(ps, 2, streamId);
		}, ResultSetMappers.INTEGER);
	}
	
	public  <T> Stream<T> findAllByStreamAndStreamId(String stream, String streamId, ResultSetMapper<T> rsm) {
		return stream(this.findByAggregateTypeAndAggregateId, ps -> {
			ps.setString(1, stream);
			dialect().setPreparedStatement(ps, 2, streamId);
		}, rsm);
	}

	private static final InsertMapper<DatabaseEvent> OPTIMIZE_INSERT_MAPPER = (dialect, ps, pojo) -> {
        ps.setString(1,  pojo.getStream());
        dialect.setPreparedStatement(ps, 2, pojo.getStreamId());

		ps.setString(3,  pojo.getStream());
		dialect.setPreparedStatement(ps, 4, pojo.getStreamId());

        if (pojo.getCorrelation() != null) {
            dialect.setPreparedStatement(ps, 5, pojo.getCorrelation());
        } else {
            ps.setNull(5, dialect.getUuidType());
        }
        ps.setTimestamp(6,  pojo.getTime());
        ps.setString(7,  pojo.getEventType());
        dialect.setPreparedStatementJsonBinary(ps, 8, pojo.getPayload());

    };

}
