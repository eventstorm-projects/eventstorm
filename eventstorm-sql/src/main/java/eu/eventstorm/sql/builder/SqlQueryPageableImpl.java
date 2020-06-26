package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.page.PageRequest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SqlQueryPageableImpl implements SqlQueryPageable {
	
	private final SelectBuilder selectBuilder;
	private final int index;
	
	public SqlQueryPageableImpl(SelectBuilder selectBuilder, int index) {
		this.selectBuilder = selectBuilder;
		this.index = index;
	}

	@Override
	public SqlQuery sqlCount(PageRequest pageable) {
		return this.selectBuilder.buildPageableCount(pageable);
	}

	@Override
	public SqlQuery sql(PageRequest pageable) {
		return this.selectBuilder.buildPageable(pageable);
	}

	@Override
	public int getIndex() {
		return this.index;
	}
	
	
	
}