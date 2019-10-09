package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Expressions {

    private Expressions() {
    }

    public static Expression eq(SqlColumn column) {
        return new SimpleStringExpression(column, "=");
    }

    public static Expression eq(SqlColumn column, String value) {
		return new SimpleStringExpression(column, "=", value);
	}

	public static Expression eq(SqlColumn column, Number value) {
		return new SimpleNumericExpression(column, "=", value);
    }

    public static Expression eq(SqlColumn column, boolean value) {
		return new SimpleBooleanExpression(column, "=", value);
    }

    public static Expression notEq(SqlColumn column, String value) {
		return new SimpleStringExpression(column, "<>", value);
    }

    public static Expression notEq(SqlColumn column, Number value) {
		return new SimpleNumericExpression(column, "<>", value);
    }

    public static Expression notEq(SqlColumn column, boolean value) {
		return new SimpleBooleanExpression(column, "<>", value);
	}

    public static Expression and(Expression left, Expression right, Expression... others) {
        return new LogicalExpression("AND", left, right, others);
    }

    public static Expression or(Expression left, Expression right, Expression... others) {
        return new LogicalExpression("OR", left, right, others);
    }
}
