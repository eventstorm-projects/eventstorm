package eu.eventstorm.sql.page;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Filters {

	ImmutableList<Expression> toExpressions();

	int size();

	PreparedStatementSetter composeWith(SqlQueryPageable query, PreparedStatementSetter pss);
	
	void forEach(Consumer<Filter> consumer);
	
	void add(String property, String operator, String value, FilterEvaluator evalutor);
	 
}