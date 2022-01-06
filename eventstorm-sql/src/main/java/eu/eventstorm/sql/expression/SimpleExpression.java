package eu.eventstorm.sql.expression;


import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class SimpleExpression<T> implements Expression {

    /**
     * Operation for this SQL (=, <, >, >=, <=).
     */
    private final String operation;

    /**
     * Name of this column.
     */
    private final SqlColumn column;

    /**
     * value
     */
    private final T value;

    protected SimpleExpression(SqlColumn column, String operation) {
        this(column, operation, null);
    }

    protected SimpleExpression(SqlColumn column, String operation, T value) {
        this.operation = operation;
        this.column = column;
        this.value = value;
    }


    /** {@inheritDoc} */
    @Override
    public String build(Dialect dialect, boolean alias) {
    	StringBuilder builder =  new StringBuilder(32);

    	if (alias) {
            builder.append(column.table().alias()).append('.');
        }

    	builder.append(column.name()).append(operation);
    	if (value == null) {
    		builder.append('?');
    	} else {
    		buildValue(builder, dialect, value);
    	}
    	return builder.toString();
    }

    @Override
    public String toString() {
    	return build(null, false);
    }

    protected abstract void buildValue(StringBuilder builder, Dialect dialect, T value);

}