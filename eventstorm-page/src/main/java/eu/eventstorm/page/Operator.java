package eu.eventstorm.page;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public enum Operator {

    EQUALS,
    IN,
    GREATER_EQUALS,
    CONTAINS
    ;

    private static final ImmutableMap<String,Operator> ALL;

    static {
        ALL = ImmutableMap.<String,Operator>builder()
                .put("[eq]", Operator.EQUALS)
                .put("[in]", Operator.IN)
                .put("[ge]", Operator.GREATER_EQUALS)
                .put("[cnt]", Operator.CONTAINS)
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
