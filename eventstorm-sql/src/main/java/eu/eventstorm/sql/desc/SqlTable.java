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

    public SqlTable(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String name() {
        return this.name;
    }

    public String alias() {
        return this.alias;
    }

    public SqlTable as(String alias) {
        return new SqlTable(name, alias);
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
            return Objects.equals(name(), ((SqlTable) o).name());
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