package eu.eventstorm.sql.expression;

public abstract class JsonPathFieldExpression extends JsonPathField implements JsonPathExpression {

    public enum Operation {
        EQUALS, GREATER, LESSER
    }

    private final Operation op;

    JsonPathFieldExpression(String field, Operation op) {
        super(field);
        this.op = op;
    }

    public Operation getOp() {
        return op;
    }

}
