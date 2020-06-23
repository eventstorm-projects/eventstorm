package eu.eventstorm.sql.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.page.Pageable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SqlQueryPageableImpl implements SqlQueryPageable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlQueryPageableImpl.class);

	private final SelectBuilder selectBuilder;
	
//	private final String sql;
//	private final int lastIndex;
//	
	
//	SqlQueryPageableImpl(String sql, int lastIndex) {
//		this.sql = sql;
//		this.lastIndex = lastIndex;
//	}

//	@Override
//	public String sql() {
//		return this.sql;
//	}
//
//	@Override
//	public void setPage(PreparedStatement ps, Pageable pageable) throws SQLException {
//		ps.setInt(lastIndex-1, pageable.getPageSize());
//		ps.setInt(lastIndex, pageable.getPageOffset());
//	}

	public SqlQueryPageableImpl(SelectBuilder selectBuilder) {
		this.selectBuilder = selectBuilder;
	}

//	@Override
//	public String toString() {
//		return this.sql;
//	}

	@Override
	public SqlQuery sqlCount(Pageable pageable) {
		return this.selectBuilder.buildPageableCount(pageable);
	}

	@Override
	public SqlQuery sql(Pageable pageable) {
		return this.selectBuilder.buildPageable(pageable);
	}
	
	
	
}