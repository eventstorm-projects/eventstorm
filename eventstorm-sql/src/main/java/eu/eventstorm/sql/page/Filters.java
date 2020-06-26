package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;

public interface Filters {

	ImmutableList<Expression> toExpressions();

	int size();

	PreparedStatementSetter composeWith(SqlQueryPageable query, PreparedStatementSetter pss);

}
