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

    private final boolean not;
	
	public InSubSelectExpression(SqlColumn column, SubSelect value, boolean not) {
		this.column = column;
		this.value = value;
		this.not = not;
	}

	@Override
	public String build(Dialect dialect, boolean alias) {
		StringBuilder builder =  new StringBuilder(128);

    	if (alias) {
            builder.append(column.table().alias()).append('.');
        }
    	builder.append(column.name());
    	if (not) {
			builder.append(" NOT");
		}
    	builder.append(" IN ( ");
    	builder.append(value.sql());
    	builder.append(" )");
    	
    	return builder.toString();
	}

}
