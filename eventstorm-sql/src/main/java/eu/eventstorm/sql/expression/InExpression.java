package eu.eventstorm.sql.expression;


import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InExpression implements Expression {

    /**
     * Name of this column.
     */
    private final SqlColumn column;
    
    private final int size;

    public InExpression(SqlColumn column, int size) {
        this.column = column;
        this.size = size;
    }

    /** {@inheritDoc} */
    @Override
    public String build(Dialect dialect, boolean alias) {
    	StringBuilder builder =  new StringBuilder(32);
    	if (alias) {
            builder.append(column.table().alias()).append('.');
        }
    	builder.append(column.name()).append(" IN ");
    	builder.append('(');
    	for (int i = 0; i < this.size; i++) {
    		builder.append("?,");	
    	}
    	builder.setLength(builder.length()-1);
    	builder.append(')');
    	return builder.toString();
    }

    @Override
    public String toString() {
    	return build(null, false);
    }

}