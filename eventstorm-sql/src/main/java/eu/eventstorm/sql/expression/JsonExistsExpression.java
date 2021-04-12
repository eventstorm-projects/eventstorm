package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JsonExistsExpression extends AbstractJsonExpression {

	JsonExistsExpression(SqlColumn column, String key, String value) {
		super(column, key, value);
	}

	@Override
	protected String applyDialectFunction(Dialect dialect, String col, String key, String value) {
		return dialect.functionJsonExists(col, key, value);
	}

}
