package eu.eventstorm.eventstore.db;

import static eu.eventstorm.eventstore.db.DatabaseEventDefinitionDescriptor.ALL;
import static eu.eventstorm.eventstore.db.DatabaseEventDefinitionDescriptor.ID;
import static eu.eventstorm.eventstore.db.DatabaseEventDefinitionDescriptor.TABLE;
import static eu.eventstorm.eventstore.db.DatabaseEventDefinitionDescriptor.VERSION;
import static eu.eventstorm.sql.builder.Order.desc;
import static eu.eventstorm.sql.expression.Expressions.eq;

import java.util.stream.Stream;

import eu.eventstorm.sql.Database;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseEventDefinitionRepository extends AbstractDatabaseEventDefinitionRepository {

	private final String findAllbyId;

	DatabaseEventDefinitionRepository(Database database) {
		super(database);
		this.findAllbyId = select(ALL).from(TABLE).where(eq(ID)).orderBy(desc(VERSION)).build();
	}

	public Stream<DatabaseEventDefinition> findAllbyId(int id) {
		return stream(findAllbyId, ps -> ps.setInt(1, id), Mappers.DATABASE_EVENT_DEFINITION);
	}
}