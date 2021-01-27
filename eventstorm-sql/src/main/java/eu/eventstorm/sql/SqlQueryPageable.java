package eu.eventstorm.sql;

import eu.eventstorm.page.PageRequest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SqlQueryPageable extends Query {

	SqlQuery sqlCount(PageRequest pageRequest);

	SqlQuery sql(PageRequest pageRequest);

	int getIndex();

}
