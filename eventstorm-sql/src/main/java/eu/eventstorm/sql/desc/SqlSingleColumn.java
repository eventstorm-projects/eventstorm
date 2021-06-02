package eu.eventstorm.sql.desc;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlSingleColumn extends SqlColumn {

    private final boolean nullable;

    private final boolean insertable;

    private final boolean updatable;
    
    public SqlSingleColumn(SqlTable table, String name, boolean nullable, boolean insertable, boolean updatable) {
        this(table, name, nullable, insertable, updatable, Strings.EMPTY);
    }
    
    public SqlSingleColumn(SqlTable table, String name, boolean nullable, boolean insertable, boolean updatable, String alias) {
    	super(table, name, alias);
        this.nullable = nullable;
        this.insertable = insertable;
        this.updatable = updatable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }
    
    @Override
    public SqlColumn newColumnFromAlias(SqlTable targetTable) {
        return new SqlSingleColumn(targetTable, name(), this.nullable, this.insertable, this.nullable);
    }

	@Override
	public SqlColumn as(String alias) {
		return new SqlSingleColumn(table(), name(), this.nullable, this.insertable, this.nullable, alias);
	}

}