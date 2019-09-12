package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */

public final class AggregateFunctions {

    private AggregateFunctions() {
    }

    public static AggregateFunction max(SqlColumn column) {
        return new SimpleAggregateFunction(column, "max");
    }

    public static AggregateFunction count(SqlColumn column) {
        return new SimpleAggregateFunction(column, "count");
    }

    public static AggregateFunction distinct(SqlColumn column) {
        return new SimpleAggregateFunction(column, "distinct");
    }

}