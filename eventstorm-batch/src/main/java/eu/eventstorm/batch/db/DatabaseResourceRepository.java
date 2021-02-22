package eu.eventstorm.batch.db;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.CREATED_AT;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.CREATED_BY;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.ID;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.META;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.TABLE;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eqJson;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseResourceRepository extends AbstractDatabaseResourceRepository {

	public DatabaseResourceRepository(Database database) {
		super(database);
	}

	public <T> Stream<T> findByMeta(LinkedHashMap<String, String> meta, ResultSetMapper<T> mapper) {

		SelectBuilder selectBuilder = select(ID, META, CREATED_BY, CREATED_AT).from(TABLE);

		if (meta.size() == 1) {
			selectBuilder.where(eqJson(META, "$." + meta.keySet().iterator().next()));
		} else if (meta.size() == 2) {
			Iterator<String> it = meta.keySet().iterator();
			selectBuilder.where(and(eqJson(META, it.next()), eqJson(META, it.next())));
		} else {
			
		}
		return stream(selectBuilder.build(), ps -> {
			int index = 1;
			for (Map.Entry<String,String> entry : meta.entrySet()) {
				ps.setString(index++, entry.getValue());
			}
		}, mapper);
	}
	
}
