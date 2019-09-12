package eu.eventstorm.sql.desc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlSingleColumn extends SqlColumn {

    private final boolean nullable;

    private final boolean insertable;

    private final boolean updatable;

    public SqlSingleColumn(SqlTable table, String name, boolean nullable, boolean insertable, boolean updatable) {
        super(table, name);
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
    protected SqlColumn newColumFromAlias(SqlTable targetTable) {
        return new SqlSingleColumn(targetTable, name(), this.nullable, this.insertable, this.nullable);
    }

}