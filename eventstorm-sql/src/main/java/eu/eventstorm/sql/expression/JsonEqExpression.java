package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JsonEqExpression extends AbstractJsonExpression {

	JsonEqExpression(SqlColumn column, String key, String value) {
		super(column, key, value);
	}

	JsonEqExpression(SqlColumn column, String key) {
		super(column, key);
	}

	@Override
	protected String applyDialectFunction(Dialect dialect, String col, String key, String value) {
		return dialect.functionJsonValue(col, key, value);
	}

}
