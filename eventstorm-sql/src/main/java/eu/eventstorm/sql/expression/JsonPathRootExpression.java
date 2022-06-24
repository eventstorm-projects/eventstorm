package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class JsonPathRootExpression implements JsonPathExpression{

    private final JsonPathExpression expression;

    JsonPathRootExpression(JsonPathExpression expression) {
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