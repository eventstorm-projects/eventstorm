package eu.eventstorm.page;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public enum Operator {

    EQUALS,
    IN,
    NOT_IN,
    GREATER_EQUALS,
    GREATER,
    LESSER_EQUALS,
    LESSER,
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH
    ;

    private static final ImmutableMap<String,Operator> ALL;

    static {
        ALL = ImmutableMap.<String,Operator>builder()
                .put("[eq]", Operator.EQUALS)
                .put("[in]", Operator.IN)
                .put("[nin]", Operator.NOT_IN)
                .put("[ge]", Operator.GREATER_EQUALS)
                .put("[gt]", Operator.GREATER)
                .put("[lt]", Operator.LESSER)
                .put("[le]", Operator.LESSER_EQUALS)
                .put("[cnt]", Operator.CONTAINS)
                .put("[sw]", Operator.STARTS_WITH)
                .put("[ew]", Operator.ENDS_WITH)
                .build();
    }

    public static Operator from(String op) {
        Operator operator = ALL.get(op);

        if (operator == null) {
            throw new PageRequestException(PageRequestException.Type.INVALID_OP, ImmutableMap.of("operator", op));
        }

        return operator;
    }

}
