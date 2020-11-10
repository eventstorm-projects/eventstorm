package eu.eventstorm.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlPrimaryKey extends SqlColumn {

	private final SqlSequence sequence;
	
    public SqlPrimaryKey(SqlTable table, SqlSequence sequence, String name) {
        super(table, name);
        this.sequence = sequence;
    }
    
    private SqlPrimaryKey(SqlTable table, SqlSequence sequence, String name, String alias) {
    	 super(table, name, alias);
    	 this.sequence = sequence;
    }

    @Override
    protected SqlColumn newColumFromAlias(SqlTable targetTable) {
        return new SqlPrimaryKey(targetTable, this.sequence, this.name());
    }
    
    public SqlSequence sequence() {
    	return this.sequence;
    }

	@Override
	public SqlColumn as(String alias) {
		return new SqlPrimaryKey(table(), sequence, name(), alias);
	}
    
}