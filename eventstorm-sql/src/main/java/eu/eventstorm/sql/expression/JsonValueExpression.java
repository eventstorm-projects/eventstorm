package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JsonValueExpression implements Expression {

    private final SqlColumn column;
    private final String path;

    public JsonValueExpression(SqlColumn column, String path) {
        this.column = column;
        this.path = path;
    }

    @Override
    public String build(Dialect dialect, boolean alias) {
        String col = "";
        if (alias) {
            col = column.table().alias() + '.';
        }
        col += column.name();

        return dialect.functionJsonValue(col, path) + "= ?";
    }

}
