package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CountAggregateFunction implements AggregateFunction {

    static final AggregateFunction INSTANCE = new CountAggregateFunction();

    private CountAggregateFunction() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(Dialect dialect, boolean alias) {
        return "count(1)";
    }

}
