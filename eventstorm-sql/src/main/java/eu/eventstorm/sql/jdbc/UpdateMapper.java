package eu.eventstorm.sql.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface UpdateMapper<T> {

    void update(Dialect dialect, PreparedStatement ps, T pojo) throws SQLException;

}
