package eu.eventstorm.sql.expression;

public final class JsonPathDeepExpression {

    private final String[] fields;

    JsonPathDeepExpression(String ...fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return fields;
    }

}
