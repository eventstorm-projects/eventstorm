package eu.eventstorm.sql.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface ResultSetMapper<T> {

    T map(Dialect dialect, ResultSet rs) throws SQLException;

}
