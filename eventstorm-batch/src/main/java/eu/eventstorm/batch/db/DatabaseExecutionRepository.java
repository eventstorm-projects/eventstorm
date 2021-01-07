package eu.eventstorm.batch.db;

import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.ALL;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.NAME;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.STARTED_AT;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.STATUS;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.TABLE;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.expression.Expressions.ge;

import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.Order;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseExecutionRepository extends AbstractDatabaseExecutionRepository {

	private final SqlQuery findLastStartedAtByName;
	private final SqlQuery findAllByDate;
	
	public DatabaseExecutionRepository(Database database) {
		super(database);
		this.findLastStartedAtByName = select(ALL).from(TABLE)
				.where(and(eq(NAME),eq(STATUS)))
				.orderBy(Order.desc(STARTED_AT))
				.limit(1)
				.build();

		this.findAllByDate = select(ALL).from(TABLE)
				.where(ge(STARTED_AT))
				.orderBy(Order.desc(STARTED_AT))
				.build();
	}

	public DatabaseExecution findLastStartedAtByName(String name, BatchStatus status) {
		return executeSelect(this.findLastStartedAtByName,ps -> {
			ps.setString(1, name);
			ps.setString(2, status.name());
		}, Mappers.DATABASE_EXECUTION);
	}

	public Stream<DatabaseExecution> findAllByDate(LocalDate date) {
		return stream(this.findAllByDate, ps -> {
			ps.setDate(1, Date.valueOf(date));
		}, Mappers.DATABASE_EXECUTION);
	}

}
