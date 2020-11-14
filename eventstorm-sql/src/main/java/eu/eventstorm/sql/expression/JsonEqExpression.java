package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JsonEqExpression implements Expression{

	private final SqlColumn column;
	
	public JsonEqExpression(SqlColumn column) {
		this.column = column;
	}


	@Override
	public String build(Dialect dialect, boolean alias) {
		String col = "";
    	if (alias) {
            col = column.table().alias() + '.';
        }
    	col += column.name();
    	
    	return dialect.functionJsonValue(col, "?" , "?");
	}

}
