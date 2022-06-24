package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class JsonPathFieldStringExpression extends JsonPathFieldExpression {

    private final String value;

    JsonPathFieldStringExpression(String field, JsonPathFieldOperation op, String value) {
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
