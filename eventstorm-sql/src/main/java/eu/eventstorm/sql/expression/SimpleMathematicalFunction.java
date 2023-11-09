package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;

final class SimpleMathematicalFunction implements MathematicalFunction {

    /**
     * Name of this column.
     */
    private final SqlColumn column;

    private final int number;

    private final char sign;

    SimpleMathematicalFunction(SqlColumn column, int number, char sign) {
        this.column = column;
        this.number = number;
        this.sign = sign;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(Dialect dialect, boolean alias) {
        StringBuilder builder = new StringBuilder(64);
        if (alias) {
            builder.append(column.table().alias()).append('.');
        }
        builder.append(column.name()).append(sign).append(number);
        return builder.toString();
    }

}
