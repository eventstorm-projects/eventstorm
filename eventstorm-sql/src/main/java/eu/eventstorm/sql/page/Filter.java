package eu.eventstorm.sql.page;

import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Filter {

	Expression getExpression();

	PreparedStatementIndexSetter getPreparedStatementIndexSetter();

}