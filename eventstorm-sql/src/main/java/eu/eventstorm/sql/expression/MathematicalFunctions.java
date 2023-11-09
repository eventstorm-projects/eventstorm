package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */

public final class MathematicalFunctions {

    private MathematicalFunctions() {
    }

    public static MathematicalFunction add(SqlColumn column, int toAdd) {
        return new SimpleMathematicalFunction(column, toAdd, '+');
    }

}