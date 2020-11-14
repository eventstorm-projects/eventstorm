package eu.eventstorm.batch.db;

import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.CREATED_AT;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.CREATED_BY;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.ID;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.META;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.TABLE;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eqJson;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseResourceRepository extends AbstractDatabaseResourceRepository {

	public DatabaseResourceRepository(Database database) {
		super(database);
	}

	public <T> Stream<T> findByMeta(ImmutableMap<String, String> meta, ResultSetMapper<T> mapper) {
		SelectBuilder selectBuilder = select(ID, META, CREATED_BY, CREATED_AT).from(TABLE);
		if (meta.size() == 1) {
			selectBuilder.where(eqJson(META));
		} else if (meta.size() == 2) {
			selectBuilder.where(and(eqJson(META), eqJson(META)));
		} else {
			
		}
		return stream(selectBuilder.build(), ps -> {
			int index = 1;
			for (String key : meta.keySet()) {
				ps.setString(index++, key);
				ps.setString(index++, meta.get(key));
			}
		}, mapper);
	}
	
}
