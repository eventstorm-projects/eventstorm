package eu.eventstorm.sql.jdbc;

import static com.google.common.collect.ImmutableMap.of;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ResultSetMappers {

	private ResultSetMappers() {
	}
	
	public static final ResultSetMapper<Long> SINGLE_LONG = (dialect, rs) -> {
		long value = rs.getLong(1);
		if (rs.next()) {
			throw new ResultSetMapperException(ResultSetMapperException.Type.MORE_THAN_ONE_RESULT, of("value", value));
		}
		return value;
	};

	public static final ResultSetMapper<Integer> SINGLE_INTEGER = (dialect, rs) -> {
		int value = rs.getInt(1);
		if (rs.next()) {
			throw new ResultSetMapperException(ResultSetMapperException.Type.MORE_THAN_ONE_RESULT, of("value", value));
		}
		return value;
	}; 
	
	public static final ResultSetMapper<String> STRING = (dialect, rs) -> rs.getString(1);
	
	public static final ResultSetMapper<Boolean> IS_EXIST = (dialect, rs) -> rs.next();

	@SuppressWarnings("serial")
	public static class ResultSetMapperException extends EventstormSqlException {

		public enum Type implements EventstormSqlExceptionType {
			MORE_THAN_ONE_RESULT
		}

		public ResultSetMapperException(Type type, ImmutableMap<String, Object> params) {
			super(type, params);
		}

	}

}
