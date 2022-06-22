package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class JsonPathLogicalExpression implements JsonPathExpression {


    public enum Operation {
        AND, OR
    }

    private final Operation operation;
    private final JsonPathFieldExpression[] fields;

    JsonPathLogicalExpression(Operation operation, JsonPathFieldExpression... fields) {
        this.operation = operation;
        this.fields = fields;
    }

    public Operation getOperation() {
        return operation;
    }

    public JsonPathFieldExpression[] getFields() {
        return fields;
    }

    @Override
    public void accept(JsonPathVisitor visitor) {
        visitor.visit(this);
    }

}
