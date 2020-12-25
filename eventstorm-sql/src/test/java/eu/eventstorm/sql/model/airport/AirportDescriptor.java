package eu.eventstorm.sql.model.airport;

import jakarta.annotation.Generated;

@Generated("eu.eventsotrm.sql.apt.PojoDescriptorGenerator")
public final class AirportDescriptor implements eu.eventstorm.sql.Descriptor {

    static final eu.eventstorm.sql.Descriptor INSTANCE = new AirportDescriptor();

    private AirportDescriptor () {
    }

    // SQL TABLE DESCRIPTOR
    public static final eu.eventstorm.sql.desc.SqlTable TABLE = new eu.eventstorm.sql.desc.SqlTable("airport", "z");

    // SQL PRIMARY KEY
    public static final eu.eventstorm.sql.desc.SqlPrimaryKey ID = new eu.eventstorm.sql.desc.SqlPrimaryKey(TABLE, null, "ident");

    // SQL PROPERTIES
    public static final eu.eventstorm.sql.desc.SqlSingleColumn TYPE = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "type", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn NAME = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "name", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn ELEVATION = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "elevation", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn CONTINENT = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "continent", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn COUNTRY = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "country", false, true, true);
    public static final eu.eventstorm.sql.desc.SqlSingleColumn REGION = new eu.eventstorm.sql.desc.SqlSingleColumn(TABLE, "region", false, true, true);
    
    // ALL COLUMNS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlColumn> ALL = com.google.common.collect.ImmutableList.of(
            ID,
            TYPE,
            NAME,
            ELEVATION,
            CONTINENT,
            COUNTRY,
            REGION);

    // ALL IDS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlPrimaryKey> IDS = com.google.common.collect.ImmutableList.of(
            ID);

    // ALL SINGLE COLUMNS
    public static final com.google.common.collect.ImmutableList<eu.eventstorm.sql.desc.SqlSingleColumn> COLUMNS = com.google.common.collect.ImmutableList.of(
    		 TYPE,
             NAME,
             ELEVATION,
             CONTINENT,
             COUNTRY,
             REGION);

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