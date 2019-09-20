package eu.eventstorm.sql.model.ex001;

import javax.annotation.Generated;

@Generated("eu.eventsotrm.sql.apt.PojoDescriptorGenerator")
public final class StudentDescriptor implements eu.eventstorm.sql.Descriptor {

    static final eu.eventstorm.sql.Descriptor INSTANCE = new StudentDescriptor();

    private StudentDescriptor () {
    }

    // SQL TABLE DESCRIPTOR
    public static final eu.eventstorm.sql.desc.SqlTable TABLE = new eu.eventstorm.sql.desc.SqlTable("student", "b");

    // SQL PRIMARY KEY
    public static final eu.eventstorm.sql.desc.SqlPrimaryKey ID = new eu.eventstorm.sql.desc.SqlPrimaryKey(TABLE, "id");

    // SQL PROPERTIES
    public static final eu.eventstorm.sql.desc.SqlSingleColumn CODE = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "code", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn AGE = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "age", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn OVERALL_RATING = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "overall_rating", true, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn CREATED_AT = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "created_at", false, true, false);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn READONLY = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "readonly", true, false, false);

    // ALL COLUMNS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlColumn> ALL = com.google.common.collect.ImmutableList.of(
            ID,
            CODE,
            AGE,
            OVERALL_RATING,
            CREATED_AT,
            READONLY);

    // ALL IDS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlPrimaryKey> IDS = com.google.common.collect.ImmutableList.of(
            ID);

    // ALL SINGLE COLUMNS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlSingleColumn> COLUMNS = com.google.common.collect.ImmutableList.of(
            CODE,
            AGE,
            OVERALL_RATING,
            CREATED_AT,
            READONLY);

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