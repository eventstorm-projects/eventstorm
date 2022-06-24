package eu.eventstorm.sql.expression;

public class JsonPathRootExpression implements JsonPathExpression{

    private final JsonPathExpression expression;

    public JsonPathRootExpression(JsonPathExpression expression) {
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