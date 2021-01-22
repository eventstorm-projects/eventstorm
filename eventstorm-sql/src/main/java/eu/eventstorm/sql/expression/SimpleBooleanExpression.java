package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SimpleBooleanExpression extends SimpleExpression<Boolean> {

	SimpleBooleanExpression(SqlColumn column, String operation, Boolean value) {
		super(column, operation, value);
	}

	@Override
	protected void buildValue(StringBuilder builder, Boolean value) {
		builder.append(value.toString());
	}

	@Override
	public int countParameter() {
		return 0;
	}
}