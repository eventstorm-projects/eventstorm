package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PageableRequestImpl implements PageRequest {

    private final int offset;
    private final int size;
    private final Filters filters;
    private final ImmutableList<Order> orders;

	PageableRequestImpl(int offset, int size, Filters filters, ImmutableList<Order> orders) {
        this.offset = offset;
        this.size = size;
        this.filters = filters;
        this.orders = orders;
	}

	@Override
	public int getOffset() {
		return this.offset;
	}
	
	@Override
	public int getSize() {
		return this.size;
	}
	
	@Override
	public Filters getFilters() {
		return this.filters;
	}

	@Override
	public ImmutableList<Order> getOrders() {
		return this.orders;
	}

	@Override
	public PageRequest next() {
		return new PageableRequestImpl(offset + size, size, filters, orders);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("offset", offset)
				.append("size", size)
				.toString();
	}
	


}
