package eu.eventstorm.sql.expression;

public final class JsonPathFieldsExpression implements JsonPathExpression {

    private final JsonPathExpression expression;


    JsonPathFieldsExpression(JsonPathExpression expression) {
        this.expression = expression;
    }

    public JsonPathExpression getExpression() {
        return expression;
    }

    @Override
    public void accept(JsonPathVisitor visitor) {
        visitor.visit(this);
    }
}
