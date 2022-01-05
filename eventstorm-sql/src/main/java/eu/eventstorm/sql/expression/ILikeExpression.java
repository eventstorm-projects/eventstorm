package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ILikeExpression implements Expression {

    /**
     * Name of this column.
     */
    private final SqlColumn column;

	public ILikeExpression(SqlColumn column) {
		this.column = column;
	}

    /** {@inheritDoc} */
    @Override
    public String build(Dialect dialect, boolean alias) {
        return dialect.ilike(column, alias);
    }

    @Override
    public String toString() {
        return "ilike " + column.toSql();
    }

}
