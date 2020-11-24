package eu.eventstorm.batch.db;

import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.ALL;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.NAME;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.STARTED_AT;
import static eu.eventstorm.batch.db.DatabaseExecutionDescriptor.TABLE;
import static eu.eventstorm.sql.jdbc.PreparedStatementSetters.setSingleString;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.expression.Expressions;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseExecutionRepository extends AbstractDatabaseExecutionRepository {

	private final SqlQuery findLastStartedAtByName;
	
	public DatabaseExecutionRepository(Database database) {
		super(database);
		this.findLastStartedAtByName = select(ALL).from(TABLE)
				.where(Expressions.eq(NAME))
				.orderBy(Order.desc(STARTED_AT))
				.limit(1)
				.build();
	}

	public DatabaseExecution findLastStartedAtByName(String name) {
		return executeSelect(this.findLastStartedAtByName, setSingleString(name), Mappers.DATABASE_EXECUTION);
	}
}
