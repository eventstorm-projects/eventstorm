package eu.eventstorm.sql.page;

import java.util.List;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.util.Strings;

/**
 * Abstract interface for pagination information.
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface PageRequest {

	int getSize();

	int getOffset();

	Filters getFilters();

	List<Order> getOrders();

	PageRequest next();

	public static PageRequestBuilder of(String query, int offset, int size) {
		return new PageRequestBuilder(query, offset, size);
	}

	public static PageRequestBuilder of(int offset, int size) {
		return new PageRequestBuilder(Strings.EMPTY, offset, size);
	}
	
}