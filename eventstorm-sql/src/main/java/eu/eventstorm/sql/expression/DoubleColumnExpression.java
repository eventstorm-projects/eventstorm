package eu.eventstorm.sql.expression;


import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DoubleColumnExpression implements Expression {

    /**
     * Operation for this SQL (=, <, >, >=, <=).
     */
    private final String operation;

    /**
     * Name of this column.
     */
    private final SqlColumn left;

    private final SqlColumn right;

    protected DoubleColumnExpression(SqlColumn left, String operation, SqlColumn right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    /** {@inheritDoc} */
    @Override
    public String build(Dialect dialect, boolean alias) {
    	StringBuilder builder =  new StringBuilder(32);

    	if (alias) {
            builder.append(left.table().alias()).append('.');
        }
    	builder.append(left.name()).append(operation);
        if (alias) {
            builder.append(right.table().alias()).append('.');
        }
        builder.append(right.name());
    	return builder.toString();
    }

    @Override
    public String toString() {
    	return build(null, false);
    }

}