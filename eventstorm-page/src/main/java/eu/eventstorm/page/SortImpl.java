package eu.eventstorm.page;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SortImpl implements Sort {

    private final boolean isAscending;
    private final String property;

    SortImpl(boolean isAscending, String property) {
        this.isAscending = isAscending;
        this.property = property;
    }

    @Override
    public boolean isAscending() {
        return this.isAscending;
    }

    @Override
    public String getProperty() {
        return this.property;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(true)
                .append("property", property)
                .append("isAscending", isAscending)
                .toString();
    }

}
