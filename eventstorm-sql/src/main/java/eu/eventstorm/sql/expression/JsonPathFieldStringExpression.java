package eu.eventstorm.sql.expression;

public final class JsonPathFieldStringExpression extends JsonPathFieldExpression {

    private final String value;

    JsonPathFieldStringExpression(String field, Operation op, String value) {
        super(field, op);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(JsonPathVisitor visitor) {
        visitor.visit(this);
    }

}
