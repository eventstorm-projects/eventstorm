package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.builder.SubSelect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InSubSelectExpression implements Expression {

	 /**
     * Name of this column.
     */
    private final SqlColumn column;

    /**
     * value
     */
    private final SubSelect value;
	
	public InSubSelectExpression(SqlColumn column, SubSelect value) {
		this.column = column;
		this.value = value;
	}

	@Override
	public String build(Dialect dialect, boolean alias) {
		StringBuilder builder =  new StringBuilder(128);

    	if (alias) {
            builder.append(column.table().alias()).append('.');
        }
    	builder.append(column.name());
    	builder.append(" in ( ");
    	builder.append(value.sql());
    	builder.append(" )");
    	
    	return builder.toString();
	}

}
