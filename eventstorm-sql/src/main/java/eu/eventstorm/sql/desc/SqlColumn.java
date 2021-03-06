package eu.eventstorm.sql.desc;

import eu.eventstorm.util.Strings;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SqlColumn implements DerivedColumn {

    /**
     * Table of this column.
     */
    private final SqlTable table;

    /**
     * Name of this column.
     */
    private final String name;
    
    /**
     * Alias of this column
     */
    private final String alias;

    protected SqlColumn(SqlTable table, String name) {
        this(table, name, Strings.EMPTY);
    }

    protected SqlColumn(SqlTable table, String name, String alias) {
        this.table = table;
        this.name = name;
        this.alias = alias;
    }

    public String name() {
        return this.name;
    }

    public SqlTable table() {
        return this.table;
    }

    public String toSql() {
        return this.name;
    }
    
    public String alias() {
    	return this.alias;
    }

    public final SqlColumn fromTable(SqlTable targetTable) {
        if (this.table.equals(targetTable)) {
            return this;
        }
        return newColumnFromAlias(targetTable);
    }

    public abstract SqlColumn as(String alias);

    public abstract SqlColumn newColumnFromAlias(SqlTable targetTable);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
        		.append("name", name)
        		.append("table", table)
        		.append("alias", alias)
        		.toString();
    }

}