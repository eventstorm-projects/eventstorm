package eu.eventstorm.sql.page;

import eu.eventstorm.sql.expression.Expression;

public interface Filter {

	Expression getExpression();

	PreparedStatementIndexSetter getPreparedStatementIndexSetter();

}