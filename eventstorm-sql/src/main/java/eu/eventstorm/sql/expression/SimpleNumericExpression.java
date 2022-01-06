package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SimpleNumericExpression extends SimpleExpression<Number> {

	SimpleNumericExpression(SqlColumn column, String operation, Number value) {
		super(column, operation, value);
	}

	@Override
	protected void buildValue(StringBuilder builder, Dialect dialect, Number value) {
		builder.append(value);
	}

	@Override
	public int countParameter() {
		return 0;
	}
}