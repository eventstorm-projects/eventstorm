package eu.eventstorm.sql.builder;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.domain.Pageable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlQueryImpl implements SqlQuery {

	private final String sql;
	
	public SqlQueryImpl(String sql) {
		this.sql = sql;
	}

	@Override
	public String sql() {
		return this.sql;
	}

	@Override
	public void setPage(PreparedStatement ps, Pageable pageable) {
	}

	@Override
	public String toString() {
		return this.sql;
	}
	
}