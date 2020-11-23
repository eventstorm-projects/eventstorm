package eu.eventstorm.sql.page;

import java.util.ArrayList;
import java.util.List;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequestBuilder {

	private final String query;
	private final int offset;
	private final int size;
	private final List<Filter> filters = new ArrayList<>(4);
	private final List<Order> orders = new ArrayList<>(4);
	
	PageRequestBuilder(String query, int offset, int size) {
		this.query = query;
		this.offset = offset;
		this.size = size;
	}
	  
	public PageRequest build() {
		return new PageableRequestImpl(query, offset, size, new FiltersImpl(filters), orders);
	}

	public PageRequestBuilder withFilter(String property, String operator, String value, Expression expression, PreparedStatementIndexSetter psis) {
		filters.add(new FilterImpl(property, operator, value, expression, psis));
		return this;
	}
	
	public PageRequestBuilder withOrder(Order order) {
		orders.add(order);
		return this;
	}
	
}
