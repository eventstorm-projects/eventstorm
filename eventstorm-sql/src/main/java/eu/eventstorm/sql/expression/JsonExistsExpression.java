package eu.eventstorm.sql.expression;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JsonExistsExpression implements Expression {

	private final SqlColumn column;
	private final String key;
	private final ImmutableList<JsonExpression> expressions;

	JsonExistsExpression(SqlColumn column, String key, ImmutableList<JsonExpression> expressions) {
		this.column = column;
		this.key = key;
		this.expressions = expressions;
	}

	@Override
	public String build(Dialect dialect, boolean alias) {
		String col = "";
		if (alias) {
			col = column.table().alias() + '.';
		}
		col += column.name();

		return dialect.functionJsonExists(col, key, expressions);
	}

	@Override
	public int countParameter() {
		return 0;
	}
}
