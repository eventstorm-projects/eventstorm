package eu.eventstorm.sql.page;

import eu.eventstorm.sql.Dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface PreparedStatementIndexSetter {

    int set(Dialect dialect, PreparedStatement ps, int index) throws SQLException;

}