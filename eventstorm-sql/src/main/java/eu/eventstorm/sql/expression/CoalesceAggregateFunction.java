package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.builder.SubSelect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CoalesceAggregateFunction implements AggregateFunction {

    private final SubSelect subSelect;
    private final int number;

    CoalesceAggregateFunction(SubSelect subSelect, int number) {
        this.subSelect = subSelect;
        this.number = number;
    }


    @Override
    public String build(Dialect dialect, boolean alias) {
        return "coalesce((" +
                subSelect.sql() +
                ")," +
                number +
                ')';
    }

}
