package eu.eventstorm.sql.page;

import eu.eventstorm.page.SinglePropertyFilter;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SqlPageRequestDescriptor {

	SqlColumn get(String property);
	
	PreparedStatementIndexSetter getPreparedStatementIndexSetter(SinglePropertyFilter filter);

}
