package eu.eventstorm.sql;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.util.Strings;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class Module {

    /**
     * Name of this module.
     */
    private final String name;

    /**
     * Name of this catalog.
     */
    private String catalog;
    
    /**
     * Name of this prefix for all tables.
     */
    private String prefix;

    /**
     * All Sql Tables for this modules
     */
    private final ImmutableList<Descriptor> descriptors;

    public Module(String name, String catalog, Descriptor... descriptors) {
        this(name, catalog, "" , descriptors);
    }
    
    public Module(String name, String catalog, String prefix, Descriptor... descriptors) {
        this.name = name;
        this.catalog = catalog;
        this.prefix = prefix;
        this.descriptors = ImmutableList.copyOf(descriptors);
    }

    public Module(String name, Descriptor... descriptors) {
        this(name, null, descriptors);
    }

    public final String name() {
        return this.name;
    }

    public final String catalog() {
        return this.catalog;
    }
    
    public final ImmutableList<Descriptor> descriptors() {
        return this.descriptors;
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this).append("name", this.name).append("catalog", this.catalog).append("descriptors", this.descriptors).toString();
    }

	public String getTableName(SqlTable table) {
		StringBuilder builder = new StringBuilder();
		if (!Strings.isEmpty(this.catalog)) {
			builder.append(this.catalog).append('.');
		}
		if (!Strings.isEmpty(this.prefix)) {
			builder.append(this.prefix);
		}
		builder.append(table.name());
		return builder.toString();
	}
	
	public String getSequenceName(SqlSequence sequence) {
		StringBuilder builder = new StringBuilder();
		if (!Strings.isEmpty(this.catalog)) {
			builder.append(this.catalog).append('.');
		}
		if (!Strings.isEmpty(this.prefix)) {
			builder.append(this.prefix);
		}
		builder.append(sequence.name());
		return builder.toString();
	}

}