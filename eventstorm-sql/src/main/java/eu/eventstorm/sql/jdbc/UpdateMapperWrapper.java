package eu.eventstorm.sql.jdbc;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.UpdateBuilder;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface UpdateMapperWrapper<T> extends UpdateMapper<T> {

    SqlQuery createSqlQuery(UpdateBuilder updateBuilder);

    ImmutableList<SqlColumn> getColumns();

}