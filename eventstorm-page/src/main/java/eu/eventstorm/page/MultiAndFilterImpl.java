package eu.eventstorm.page;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class MultiAndFilterImpl implements MultiAndFilter {

    private final ImmutableList<Filter> filters;

    MultiAndFilterImpl(Filter[] filters) {
        ImmutableList.Builder<Filter> builder = ImmutableList.builder();
        for (Filter filter : filters) {
            if (filter != EmptyFilter.INSTANCE) {
                builder.add(filter);
            }
        }
        this.filters = builder.build();
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visitBegin(this);
        filters.forEach(filter -> filter.accept(visitor));
        visitor.visitEnd(this);
    }

}
