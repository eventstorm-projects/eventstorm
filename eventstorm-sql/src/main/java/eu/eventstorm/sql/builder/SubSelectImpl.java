package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SubSelectImpl implements SubSelect {

	private final SqlQuery query;
	private final String alias;
	private final SqlTable table;
	
	public SubSelectImpl(SqlQuery query, String alias) {
		super();
		this.query = query;
		this.alias = alias;
		this.table = new SqlTable("", alias);
	}

	@Override
	public String sql() {
		return query.sql();
	}

	@Override
	public String alias() {
		return this.alias;
	}

	@Override
	public SqlTable table() {
		return this.table;
	}

	@Override
	public SqlColumn column(SqlColumn column) {
		return column.fromTable(table);
	}

}
