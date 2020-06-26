package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.builder.Order;

/**
 * Abstract interface for pagination information.
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface PageRequest {

    int getSize();

    int getOffset();

    Filters getFilters();
    
    ImmutableList<Order> getOrders();
    
    PageRequest next();

    public static PageRequestBuilder of(int offset, int size) {
       return new PageRequestBuilder(offset, size);
    }
    
}