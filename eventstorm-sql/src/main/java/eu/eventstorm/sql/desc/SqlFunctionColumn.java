package eu.eventstorm.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SqlFunctionColumn extends SqlColumn {

    private final String function;

    public SqlFunctionColumn(String function, SqlColumn column) {
        super(column.table(), column.name());
        this.function = function;
    }
    
    private SqlFunctionColumn(String function, SqlTable table, String column, String alias) {
        super(table, column, alias);
        this.function = function;
    }

    @Override
    public String toSql() {
        return new StringBuilder()
        		.append(this.function).append('(').append(name()).append(')')
        		// TODO add alias
        		.toString();
    }

    @Override
    public SqlColumn newColumnFromAlias(SqlTable targetTable) {
        return null;
    }

	@Override
	public SqlColumn as(String alias) {
		return new SqlFunctionColumn(function, table(), name(), alias);
	}

}
