package eu.eventstorm.batch.db;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.expression.JsonPathFieldExpression;
import eu.eventstorm.sql.expression.JsonPathFieldExpression.Operation;
import eu.eventstorm.sql.jdbc.PreparedStatementSetters;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.CREATED_AT;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.CREATED_BY;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.ID;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.META;
import static eu.eventstorm.batch.db.DatabaseResourceDescriptor.TABLE;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.jsonExists;
import static eu.eventstorm.sql.expression.JsonPathExpressions.fields;
import static eu.eventstorm.sql.expression.JsonPathExpressions.field;
import static eu.eventstorm.sql.expression.JsonPathExpressions.root;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseResourceRepository extends AbstractDatabaseResourceRepository {

	public DatabaseResourceRepository(Database database) {
		super(database);
	}

	public <T> Stream<T> findByMeta(LinkedHashMap<String, String> meta, ResultSetMapper<T> mapper) {

		SelectBuilder selectBuilder = select(ID, META, CREATED_BY, CREATED_AT).from(TABLE);
		ImmutableList.Builder<Expression> builder = ImmutableList.builder();
		meta.forEach((key,value) -> {
			// $.[?(@.expensive==10)]
			//builder.add(jsonExists(META, "$.[?(@."+ key +"==\""+ value +"\")]"));
			//"$.[?(@."+ key +"==\""+ value +"\")]"));
			builder.add(jsonExists(META, root(fields(field(key, Operation.EQUALS, value)))));
		});
		ImmutableList<Expression> expressions = builder.build();

		if (expressions.size() == 1) {
			selectBuilder.where(expressions.get(0));
		} else {
			selectBuilder.where(and(expressions));
		}

		return stream(selectBuilder.build(), PreparedStatementSetters.noParameter(), mapper);
	}
	
}
