package eu.eventstorm.sql.model.json;

//@Generated("eu.eventsotrm.sql.apt.PojoDescriptorGenerator")
public final class SpanDescriptor implements eu.eventstorm.sql.Descriptor {

    static final eu.eventstorm.sql.Descriptor INSTANCE = new SpanDescriptor();

    private SpanDescriptor () {
    }

    // SQL TABLE DESCRIPTOR
    public static final eu.eventstorm.sql.desc.SqlTable TABLE = new eu.eventstorm.sql.desc.SqlTable("span", "a");

    // SQL PRIMARY KEY
    public static final eu.eventstorm.sql.desc.SqlPrimaryKey ID = new eu.eventstorm.sql.desc.SqlPrimaryKey(TABLE, null, "id");

    // SQL PROPERTIES
    public static final eu.eventstorm.sql.desc.SqlSingleColumn CONTENT = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "content", false, true, true);

    // ALL COLUMNS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlColumn> ALL = com.google.common.collect.ImmutableList.of(
            ID,
            CONTENT);

    // ALL IDS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlPrimaryKey> IDS = com.google.common.collect.ImmutableList.of(
            ID);

    // ALL SINGLE COLUMNS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlSingleColumn> COLUMNS = com.google.common.collect.ImmutableList.of(
            CONTENT);

    public eu.eventstorm.sql.desc.SqlTable table() {
        return TABLE;
    }

    public com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlSingleColumn> columns() {
        return COLUMNS;
    }

    public com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlPrimaryKey> ids() {
        return IDS;
    }
}