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

    @Override
    protected SqlColumn newColumFromAlias(SqlTable targetTable) {
        return new SqlPrimaryKey(targetTable, this.sequence, this.name());
    }
    
    public SqlSequence sequence() {
    	return this.sequence;
    }
    
}