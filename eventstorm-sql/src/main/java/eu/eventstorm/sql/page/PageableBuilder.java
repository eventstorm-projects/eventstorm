package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.expression.Expression;

public final class PageableBuilder {

	private final int offset;
	private final int size;
	private final ImmutableList.Builder<Expression> filters = ImmutableList.builder();
	private final ImmutableList.Builder<Sort> sorts = ImmutableList.builder();
	
	PageableBuilder(int offset, int size) {
		this.offset = offset;
		this.size = size;
	}
	
	public Pageable build() {
		return new PageableImpl(offset, size, filters.build(), sorts.build());
	}

	public PageableBuilder withFilter(Expression expression) {
		filters.add(expression);
		return this;
	}
}
