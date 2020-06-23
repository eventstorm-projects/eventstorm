package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PageableImpl implements Pageable {

    private final int offset;
    private final int size;
    private final ImmutableList<Expression> filters;
    private final ImmutableList<Sort> sorts;

	PageableImpl(int offset, int size, ImmutableList<Expression> filters, ImmutableList<Sort> sorts) {
        this.offset = offset;
        this.size = size;
        this.filters = filters;
        this.sorts = sorts;
	}


	@Override
	public int getPageOffset() {
		return this.offset;
	}

	
	@Override
	public int getPageSize() {
		return this.size;
	}
	
	@Override
	public ImmutableList<Expression> getFilters() {
		return this.filters;
	}

	@Override
	public ImmutableList<Sort> getSorts() {
		return this.sorts;
	}

	@Override
	public Pageable next() {
		return new PageableImpl(offset + size, size, filters, sorts);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("offset", offset)
				.append("size", size)
				.toString();
	}
	


}
