package eu.eventstorm.page;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Filters {

    private Filters(){}

    public static Filter newInstance(String property, Operator operator, String raw, List<String> values) {
        return new FilterImpl(property, operator, raw, values);
    }

}
