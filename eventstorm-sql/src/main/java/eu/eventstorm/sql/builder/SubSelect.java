package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SubSelect {

	String sql();

	String alias();

	SqlTable table();

	SqlColumn column(SqlColumn column);

}
