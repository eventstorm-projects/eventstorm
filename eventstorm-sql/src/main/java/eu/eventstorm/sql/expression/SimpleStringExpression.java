package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SimpleStringExpression extends SimpleExpression<String> {

	SimpleStringExpression(SqlColumn column, String operation, String value) {
		super(column, operation, value);
	}

	SimpleStringExpression(SqlColumn column, String operation) {
		super(column, operation);
	}

	@Override
	protected void buildValue(StringBuilder builder, String value) {
		builder.append('\'').append(value).append('\'');
	}

}