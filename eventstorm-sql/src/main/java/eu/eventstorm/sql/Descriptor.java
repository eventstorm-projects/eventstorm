package eu.eventstorm.sql;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Descriptor {

    ImmutableList<SqlSingleColumn> columns();

    ImmutableList<SqlPrimaryKey> ids();

    SqlTable table();

}
