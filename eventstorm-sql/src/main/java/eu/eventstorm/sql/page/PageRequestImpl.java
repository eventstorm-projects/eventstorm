package eu.eventstorm.sql.page;

import java.util.List;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.util.Strings;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PageRequestImpl implements PageRequest {

	private final String query;
    private final int offset;
    private final int size;
    private final Filters filters;
    private final List<Order> orders;

	PageRequestImpl(String query, int offset, int size, Filters filters, List<Order> orders) {
		this.query = query;
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
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public PageRequest next() {
		return new PageRequestImpl(Strings.EMPTY, offset + size, size, filters, orders);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("query", query)
				.append("offset", offset)
				.append("size", size)
				.append("filter", this.filters)
				.append("orders", this.orders)
				.toString();
	}

}