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

    public static AggregateFunction count() {
        return CountAggregateFunction.INSTANCE;
    }

    public static AggregateFunction distinct(SqlColumn column) {
        return new SimpleAggregateFunction(column, "distinct");
    }

    public static AggregateFunction upper(SqlColumn column) {
        return new SimpleAggregateFunction(column, "upper");
    }

    public static AggregateFunction lower(SqlColumn column) {
        return new SimpleAggregateFunction(column, "lower");
    }
    
    public static AggregateFunction rowNumber() {
        return new RowNumberAggregateFunction();
    }
    
    public static AggregateFunction rowNumber(OverPartition overPartition) {
        return new RowNumberAggregateFunction(overPartition);
    }

}