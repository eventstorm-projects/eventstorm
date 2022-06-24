package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.expression.JsonPathFieldExpression;
import eu.eventstorm.sql.expression.JsonPathFieldOperation;
import eu.eventstorm.sql.expression.JsonPathFieldStringExpression;
import eu.eventstorm.sql.expression.JsonPathLogicalExpression;
import eu.eventstorm.sql.expression.JsonPathRootExpression;
import eu.eventstorm.sql.expression.JsonPathVisitor;

abstract class AbstractJsonPathVisitor implements JsonPathVisitor {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void visit(JsonPathLogicalExpression expression) {
        builder.append('(');
        JsonPathFieldExpression[] fields = expression.getFields();
        int i = 0;
        for (; i < fields.length - 1; i++) {
            fields[i].accept(this);
            builder.append(op(expression.getOperation()));
        }
        fields[i].accept(this);
        builder.append(')');
    }

    @Override
    public void visit(JsonPathFieldStringExpression expression) {
        builder.append("(@.");
        builder.append(expression.getField());
        builder.append(op(expression.getOp()));
        builder.append("\"");
        builder.append(expression.getValue());
        builder.append("\")");
    }

    @Override
    public void visit(JsonPathRootExpression expression) {
        builder.append("$");
        expression.getExpression().accept(this);
    }

    @Override
    public String toString() {
        return builder.toString();
    }


    protected final StringBuilder getBuilder() {
        return builder;
    }


    protected static String op(JsonPathFieldOperation op) {
        if (JsonPathFieldOperation.EQUALS == op) {
            return "==";
        }
        if (JsonPathFieldOperation.GREATER == op) {
            return ">";
        }
        return "<";
    }

    private static String op(JsonPathLogicalExpression.Operation operation) {
        if (JsonPathLogicalExpression.Operation.AND == operation) {
            return " && ";
        }
        return " || ";
    }
}
