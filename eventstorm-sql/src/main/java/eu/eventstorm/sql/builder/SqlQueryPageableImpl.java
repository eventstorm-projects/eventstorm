package eu.eventstorm.sql.builder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.domain.Pageable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SqlQueryPageableImpl implements SqlQuery {

	private final String sql;
	private final int lastIndex;
	
	SqlQueryPageableImpl(String sql, int lastIndex) {
		this.sql = sql;
		this.lastIndex = lastIndex;
	}

	@Override
	public String sql() {
		return this.sql;
	}

	@Override
	public void setPage(PreparedStatement ps, Pageable pageable) throws SQLException {
		ps.setInt(lastIndex-1, pageable.getPageSize());
		ps.setInt(lastIndex, pageable.getPageSize() * pageable.getPageNumber());
	}
	
}