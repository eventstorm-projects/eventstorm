package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequestBuilder {

	private final int offset;
	private final int size;
	private final ImmutableList.Builder<Filter> filters = ImmutableList.builder();
	private final ImmutableList.Builder<Order> orders = ImmutableList.builder();
	
	PageRequestBuilder(int offset, int size) {
		this.offset = offset;
		this.size = size;
	}
	  
	public PageRequest build() {
		return new PageableRequestImpl(offset, size, new FiltersImpl(filters.build()), orders.build());
	}

	public PageRequestBuilder withFilter(Expression expression, PreparedStatementIndexSetter psis) {
		filters.add(new FilterImpl(expression, psis));
		return this;
	}
	
	public PageRequestBuilder withFilter(Expression expression) {
		filters.add(new FilterImpl(expression));
		return this;
	}

	public PageRequestBuilder withOrder(Order order) {
		orders.add(order);
		return this;
	}
	
}
