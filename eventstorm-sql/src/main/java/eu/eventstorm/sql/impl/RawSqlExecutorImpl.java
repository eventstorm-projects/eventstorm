package eu.eventstorm.sql.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import eu.eventstorm.sql.RawSqlExecutor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class RawSqlExecutorImpl implements RawSqlExecutor {

	private final DataSource dataSource;

	public RawSqlExecutorImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void execute(String ... sql) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			try (Statement statement = conn.createStatement()) {
				for (String s : sql) {
					statement.execute(s);
				}
			}
		}
	}

	@Override
	public void call(String...sql) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			for (String s : sql) {
				try (CallableStatement statement = conn.prepareCall(s)) {
					statement.execute();
				}
			}
		}
	}
	
}