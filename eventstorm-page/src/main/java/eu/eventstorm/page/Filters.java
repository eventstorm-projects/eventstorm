package eu.eventstorm.page;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Filters {

    private Filters() {
    }

    @Deprecated
    public static Filter newInstance(String property, Operator operator, String raw, List<String> values) {
        return newProperty(property, operator, raw, values);
    }

    public static Filter newProperty(String property, Operator operator, String raw, List<String> values) {
        return new SinglePropertyFilterImpl(property, operator, raw, values);
    }

    public static Filter newProperty(String property, Operator operator, String raw) {
        return new SinglePropertyFilterImpl(property, operator, raw, of(raw));
    }

    public static Filter newAnd(Filter... filters) {
        if (filters.length == 1) {
            return filters[0];
        }
        if (filters.length == 2) {
            return newAnd(filters[0], filters[1]);
        }
        return new MultiAndFilterImpl(filters);
    }

    public static Filter newAnd(Filter left, Filter right) {
        if (left == EmptyFilter.INSTANCE) {
            return right;
        }
        // left is not empty
        if (right == EmptyFilter.INSTANCE) {
            return left;
        } else {
            return new AndFilterImpl(left, right);
        }

    }

    public static Filter newOr(Filter left, Filter right) {
        return new OrFilterImpl(left, right);
    }
}
