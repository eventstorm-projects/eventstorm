package eu.eventstorm.sql.expression;

public class JsonPathFieldsExpression implements JsonPathExpression {

    private final JsonPathExpression expression;


    public JsonPathFieldsExpression(JsonPathExpression expression) {
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
