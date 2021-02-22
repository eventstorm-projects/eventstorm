package eu.eventstorm.sql.expression;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.builder.SubSelect;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Expressions {

    private Expressions() {
    }

    public static Expression eq(SqlColumn column) {
        return new ParameterSimpleExpression(column, "=");
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

    public static Expression eq(SqlColumn left, SqlColumn right) {
        return new DoubleColumnExpression(left, "=", right);
    }
    
    public static Expression notEq(SqlColumn column) {
		return new ParameterSimpleExpression(column, "<>");
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

    public static Expression ge(SqlColumn column) {
		return new ParameterSimpleExpression(column, ">=");
    }
    
    public static Expression ge(SqlColumn column, Number value) {
		return new SimpleNumericExpression(column, ">=", value);
    }
    
    public static Expression le(SqlColumn column) {
		return new ParameterSimpleExpression(column, "<=");
    }
    
    public static Expression le(SqlColumn column, Number value) {
		return new SimpleNumericExpression(column, "<=", value);
    }
    
    public static Expression gt(SqlColumn column) {
		return new ParameterSimpleExpression(column, ">");
    }
    
    public static Expression gt(SqlColumn column, Number value) {
		return new SimpleNumericExpression(column, ">", value);
    }

    public static Expression lt(SqlColumn column) {
		return new ParameterSimpleExpression(column, "<");
    }
    
    public static Expression lt(SqlColumn column, Number value) {
		return new SimpleNumericExpression(column, "<", value);
    }
    
    public static Expression and(Expression left, Expression right) {
        return new LogicalExpression("AND", ImmutableList.of(left, right));
    }
    
    public static Expression and(Expression left, Expression right, Expression... others) {
        return new LogicalExpression("AND", ImmutableList.<Expression>builder().add(left).add(right).addAll(Arrays.asList(others)).build());
    }
    
    public static Expression and(ImmutableList<Expression> expressions) {
		return new LogicalExpression("AND", expressions);
	}

    public static Expression or(Expression left, Expression right) {
        return new LogicalExpression("OR", ImmutableList.of(left, right));
    }
    
    public static Expression or(Expression left, Expression right, Expression... others) {
        return new LogicalExpression("OR", ImmutableList.<Expression>builder().add(left).add(right).addAll(Arrays.asList(others)).build());
    }

    public static Expression in(SqlColumn column, SubSelect value) {
		return new InSubSelectExpression(column, value, false);
    }

    public static Expression notIn(SqlColumn column, SubSelect value) {
        return new InSubSelectExpression(column, value, true);
    }

    public static Expression in(SqlColumn column, int size) {
		return new InExpression(column, size);
    }
    
    public static Expression like(SqlColumn column) {
		return new LikeExpression(column);
    }
    
    public static Expression raw(String raw) {
  		return (dialect, alias) -> raw;
    }

	public static Expression eqJson(SqlColumn column, String key) {
		return new JsonEqExpression(column, key);
	}
    
}
