package eu.eventstorm.sql;

import java.sql.SQLException;

public interface RawSqlExecutor {

	void execute(String ... sql) throws SQLException;
	
	void call(String ... sql) throws SQLException;
	
}
