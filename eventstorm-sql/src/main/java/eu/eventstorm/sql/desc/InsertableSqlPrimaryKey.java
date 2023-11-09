package eu.eventstorm.sql.desc;

public final class InsertableSqlPrimaryKey {

    private final SqlPrimaryKey column;

    private final DerivedColumn value;

    public InsertableSqlPrimaryKey(SqlPrimaryKey key) {
        this(key, key);
    }

    public InsertableSqlPrimaryKey(SqlPrimaryKey column, DerivedColumn value) {
        this.column = column;
        this.value = value;
    }

    public SqlPrimaryKey getColumn() {
        return column;
    }

    public DerivedColumn getValue() {
        return value;
    }

}