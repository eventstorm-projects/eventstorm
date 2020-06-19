package eu.eventstorm.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.domain.Pageable;

public interface SqlQuery {

	String sql();

	void setPage(PreparedStatement ps, Pageable pageable) throws SQLException;

}
