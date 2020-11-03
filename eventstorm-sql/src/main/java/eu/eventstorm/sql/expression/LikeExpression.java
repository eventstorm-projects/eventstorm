package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class LikeExpression implements Expression {

    /**
     * Name of this column.
     */
    private final SqlColumn column;
    
	public LikeExpression(SqlColumn column) {
		this.column = column;
	}

    /** {@inheritDoc} */
    @Override
    public String build(Dialect dialect, boolean alias) {
    	StringBuilder builder =  new StringBuilder(32);
    	if (alias) {
            builder.append(column.table().alias()).append('.');
        }
    	builder.append(column.name()).append(" LIKE ?");
    	return builder.toString();
    }

}
