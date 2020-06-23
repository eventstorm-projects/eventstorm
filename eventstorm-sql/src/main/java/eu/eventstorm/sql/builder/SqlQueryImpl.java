package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.SqlQuery;

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
	public String toString() {
		return this.sql;
	}
	
}