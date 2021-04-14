package eu.eventstorm.sql.expression;

final class DefaultJsonExpression implements JsonExpression {

    private final String field;
    private final JsonOperation operation;
    private final Object value;

    DefaultJsonExpression(String field, JsonOperation operation, Object value) {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public JsonOperation getOperation() {
        return operation;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
