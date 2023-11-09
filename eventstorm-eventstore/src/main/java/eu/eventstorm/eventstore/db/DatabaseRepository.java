package eu.eventstorm.eventstore.db;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.COLUMNS;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.EVENT_TYPE;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.IDS;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.PAYLOAD;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.REVISION;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.STREAM;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.STREAM_ID;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.TABLE;
import static eu.eventstorm.eventstore.db.DatabaseEventDescriptor.TIME;
import static eu.eventstorm.sql.builder.Order.asc;
import static eu.eventstorm.sql.expression.AggregateFunctions.coalesce;
import static eu.eventstorm.sql.expression.AggregateFunctions.max;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.expression.MathematicalFunctions.add;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.builder.SubSelects;
import eu.eventstorm.sql.desc.InsertableSqlPrimaryKey;
import eu.eventstorm.sql.expression.AggregateFunction;
import eu.eventstorm.sql.expression.AggregateFunctions;
import eu.eventstorm.sql.expression.MathematicalFunctions;
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

	// insert into epvote.krn_event_store(stream, stream_id, revision, time, event_type, payload) select 'ballot', '00001766-6915-6c6b-0064-006400000761',
// coalesce((select revision+1 from epvote.krn_event_store where stream='ballot' and stream_id='00001766-6915-6c6b-0064-006400000761' order by revision desc limit 1), 0) as revision, now(), 'GeneratedBallot',
// '{"hash": "A94D372C64EACA17EAE267C3F54F4CE8A92EC0170C9DDE26D4503FED5C15E417", "isNew": true, "voter": "eici-testscharge240", "ballot": "00001766-6915-6c6b-0064-006400000761", "revision": 1, "timestamp": "2022-10-27T15:27:34.549+02:00",
// "votingPeriod": "00001764-3ba6-65f7-0064-006400000025"}' returning revision;


	DatabaseRepository(Database database) {
		super(database);
		this.optimizeInsert = insert(of(
						new InsertableSqlPrimaryKey(STREAM),
						new InsertableSqlPrimaryKey(STREAM_ID),
						new InsertableSqlPrimaryKey(REVISION, coalesce(SubSelects.from(select(add(REVISION,1)).from(TABLE).where(and(eq(STREAM), eq(STREAM_ID))).orderBy(Order.desc(REVISION)).limit(1).build()), 1))),
					COLUMNS, TABLE)
				//.returning(REVISION)
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
