package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class JsonPathFieldExpression extends JsonPathField implements JsonPathExpression {

    private final JsonPathFieldOperation op;

    JsonPathFieldExpression(String field, JsonPathFieldOperation op) {
        super(field);
        this.op = op;
    }

    public JsonPathFieldOperation getOp() {
        return op;
    }

}
