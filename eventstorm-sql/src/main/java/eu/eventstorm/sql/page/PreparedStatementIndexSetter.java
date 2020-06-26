package eu.eventstorm.sql.page;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@FunctionalInterface
public interface PreparedStatementIndexSetter {

    void set(PreparedStatement ps, int index) throws SQLException;

}