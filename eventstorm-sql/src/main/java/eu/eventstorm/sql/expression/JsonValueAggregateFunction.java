package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

final class JsonValueAggregateFunction implements AggregateFunction {

    private final SqlColumn column;
    private final JsonPathDeepExpression path;

    public JsonValueAggregateFunction(SqlColumn column, JsonPathDeepExpression path) {
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

        return dialect.functionJsonValue(col, path);
    }
}
