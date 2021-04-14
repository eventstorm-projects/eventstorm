package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class JsonExpressions {

    private JsonExpressions(){}

    public static JsonExpression eq(String field, String value) {
        return new DefaultJsonExpression(field, JsonOperation.EQUALS, value);
    }

    public static JsonExpression eq(String field, Number value) {
        return new DefaultJsonExpression(field, JsonOperation.EQUALS, value);
    }

}
