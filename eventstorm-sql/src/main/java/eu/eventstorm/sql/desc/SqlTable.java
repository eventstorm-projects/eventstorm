package eu.eventstorm.sql.desc;

import java.util.Objects;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlTable {

    /**
     * Name of this table.
     */
    private final String name;

    private final String alias;

    private final SqlTable parent;

    private SqlTable(String name, String alias, SqlTable parent) {
        this.name = name;
        this.alias = alias;
        this.parent = parent;
    }

    public SqlTable(String name, String alias) {
        this(name, alias, null);
    }

    public String name() {
        return this.name;
    }

    public String alias() {
        return this.alias;
    }

    public SqlTable parent() {
        return this.parent;
    }

    public SqlTable as(String alias) {
        return new SqlTable(name, alias, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("alias", alias)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof SqlTable) {
            return Objects.equals(name(), ((SqlTable) o).name()) && Objects.equals(alias(), ((SqlTable) o).alias());
        }

        return false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.alias.hashCode();
    }

}