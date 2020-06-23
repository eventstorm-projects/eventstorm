package eu.eventstorm.sql;

import eu.eventstorm.sql.page.Pageable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SqlQueryPageable extends Query {

	//void setPage(PreparedStatement ps, Pageable pageable) throws SQLException;

	SqlQuery sqlCount(Pageable pageable);

	SqlQuery sql(Pageable pageable);

}
