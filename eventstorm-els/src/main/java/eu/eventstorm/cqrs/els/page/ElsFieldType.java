package eu.eventstorm.cqrs.els.page;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ElsFieldType {

    KEYWORD(s -> s.substring(1, s.length() - 1),() -> ".keyword"),
    TEXT(s -> s.substring(1, s.length() - 1), () -> ""),
    NUMBER(s -> s,()-> ""),
    NESTED(s -> s.substring(1, s.length() - 1),()-> ""),
    ID(s -> s.substring(1, s.length() - 1),()-> ""),
    DATE(s -> s.substring(1, s.length() - 1),()-> "");

    private final Function<String, String> unwrap;
    private final Supplier<String> termQueryField;

    ElsFieldType(Function<String, String> unwrap, Supplier<String> termQueryField){
        this.unwrap = unwrap;
        this.termQueryField = termQueryField;
    }

    public String unwrap(String value) {
        return unwrap.apply(value);
    }

    public String termQueryField() {
        return termQueryField.get();
    }
}
