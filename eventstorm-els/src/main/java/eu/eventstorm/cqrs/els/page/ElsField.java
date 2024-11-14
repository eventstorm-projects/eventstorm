package eu.eventstorm.cqrs.els.page;

public record ElsField(String name, ElsFieldType type) {

    String unwrap(String value) {
        return type.unwrap(value);
    }

    public String termQueryField() {
        return name + type.termQueryField();
    }

}
