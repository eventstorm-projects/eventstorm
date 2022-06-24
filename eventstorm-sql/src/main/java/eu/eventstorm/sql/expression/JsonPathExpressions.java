package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class JsonPathExpressions {

    public static JsonPathExpression array(JsonPathExpression expression) {
        return new JsonPathArrayExpression(expression);
    }

    public static JsonPathExpression root(JsonPathExpression expression) {
        return new JsonPathRootExpression(expression);
    }

    public static JsonPathDeepExpression path(String ... fields) {
        return new JsonPathDeepExpression(fields);
    }

    public static JsonPathExpression fields(JsonPathLogicalExpression expression) {
        return new JsonPathFieldsExpression(expression);
    }

    public static JsonPathExpression fields(JsonPathFieldExpression expression) {
        return new JsonPathFieldsExpression(expression);
    }


    public static JsonPathLogicalExpression and(JsonPathFieldExpression... fields) {
        return new JsonPathLogicalExpression(JsonPathLogicalExpression.Operation.AND, fields);
    }


    public static JsonPathFieldExpression field(String field, JsonPathFieldOperation op, String value) {
        return new JsonPathFieldStringExpression(field, op, value);
    }

    public static JsonPathField field(String field) {
        return new JsonPathField(field);
    }


}
