package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractJsonExpression implements Expression {

    private final SqlColumn column;
    private final String key;
    private final String value;

    public AbstractJsonExpression(SqlColumn column, String key, String value) {
        this.column = column;
        this.key = key;
        this.value = value;
    }

    public AbstractJsonExpression(SqlColumn column, String key) {
        this.column = column;
        this.key = key;
        this.value = "?";
    }

    @Override
    public String build(Dialect dialect, boolean alias) {
        String col = "";
        if (alias) {
            col = column.table().alias() + '.';
        }
        col += column.name();

        return applyDialectFunction(dialect, col, key, value);
    }

    protected abstract String applyDialectFunction(Dialect dialect, String col, String key, String value);
}
