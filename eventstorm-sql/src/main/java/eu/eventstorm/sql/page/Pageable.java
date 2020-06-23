package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.expression.Expression;

/**
 * Abstract interface for pagination information.
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Pageable {

    int getPageSize();

    int getPageOffset();

    ImmutableList<Expression> getFilters();
    
    ImmutableList<Sort> getSorts();
    
    Pageable next();

    public static PageableBuilder of(int offset, int size) {
       return new PageableBuilder(offset, size);
    }
    
}