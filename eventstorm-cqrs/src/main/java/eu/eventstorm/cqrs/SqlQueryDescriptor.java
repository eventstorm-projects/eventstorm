package eu.eventstorm.cqrs;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.page.PreparedStatementIndexSetter;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SqlQueryDescriptor {

	SqlColumn get(String proper);
	
	PreparedStatementIndexSetter getPreparedStatementIndexSetter(String property, String value);

}
