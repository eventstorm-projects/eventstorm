package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ParameterSimpleExpression extends SimpleExpression<Void> {

	ParameterSimpleExpression(SqlColumn column, String operation) {
		super(column, operation);
    }

	@Override
	protected void buildValue(StringBuilder builder, Dialect dialect, Void value) {
        throw new IllegalStateException();
	}

}