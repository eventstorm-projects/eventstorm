package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SimpleNumericExpression extends SimpleExpression<Number> {

	SimpleNumericExpression(SqlColumn column, String operation, Number value) {
		super(column, operation, value);
	}

	@Override
	protected void buildValue(StringBuilder builder, Number value) {
		builder.append(value);
	}

}