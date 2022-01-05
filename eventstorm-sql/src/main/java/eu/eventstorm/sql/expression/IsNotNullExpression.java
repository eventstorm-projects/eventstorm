package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class IsNotNullExpression implements Expression {

    private final SqlColumn column;

    IsNotNullExpression(SqlColumn column) {
        this.column = column;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(Dialect dialect, boolean alias) {
        StringBuilder builder = new StringBuilder(32);

        if (alias) {
            builder.append(column.table().alias()).append('.');
        }

        builder.append(column.name()).append(" IS NOT NULL");
        return builder.toString();
    }

    @Override
    public String toString() {
        return build(null, false);
    }

}