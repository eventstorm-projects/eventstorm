package eu.eventstorm.sql.expression;

public class JsonPathField {

    private final String field;

    JsonPathField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

}
