package eu.eventstorm.sql.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface PreparedStatementSetter {

    void set(PreparedStatement ps) throws SQLException;

}