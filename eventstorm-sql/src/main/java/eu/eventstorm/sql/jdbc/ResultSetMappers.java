package eu.eventstorm.sql.jdbc;

import static com.google.common.collect.ImmutableMap.of;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ResultSetMappers {

	private ResultSetMappers() {
	}
	
	public static final ResultSetMapper<Long> LONG = (dialect, rs) ->  rs.getLong(1);
	public static final ResultSetMapper<Integer> INTEGER = (dialect, rs) -> rs.getInt(1);
	public static final ResultSetMapper<Short> SHORT = (dialect, rs) -> rs.getShort(1);
	public static final ResultSetMapper<Byte> BYTE = (dialect, rs) -> rs.getByte(1);
	public static final ResultSetMapper<String> STRING = (dialect, rs) ->  rs.getString(1);

	public static final ResultSetMapper<Long> SINGLE_LONG = (dialect, rs) -> {
		long value = rs.getLong(1);
		checkOnlyOneResult(rs, value);
		return value;
	};

	public static final ResultSetMapper<Integer> SINGLE_INTEGER = (dialect, rs) -> {
		int value = rs.getInt(1);
		checkOnlyOneResult(rs, value);
		return value;
	};
	
	public static final ResultSetMapper<Long> LONG_NULLABLE = (dialect, rs) -> {
		long value = rs.getLong(1);
		if (rs.wasNull()) {
			return null;
		}
		return value;
	};
	
	public static final ResultSetMapper<Integer> INTEGER_NULLABLE = (dialect, rs) -> {
		int value = rs.getInt(1);
		if (rs.wasNull()) {
			return null;
		}
		return value;
	};

	public static final ResultSetMapper<Short> SHORT_NULLABLE = (dialect, rs) -> {
		short value = rs.getShort(1);
		if (rs.wasNull()) {
			return null;
		}
		return value;
	};

	public static final ResultSetMapper<Byte> BYTE_NULLABLE = (dialect, rs) -> {
		byte value = rs.getByte(1);
		if (rs.wasNull()) {
			return null;
		}
		return value;
	};

	public static final ResultSetMapper<String> SINGLE_STRING = (dialect, rs) -> {
		String value = rs.getString(1);
		checkOnlyOneResult(rs, value);
		return value;
	};
	
	public static final ResultSetMapper<String> STRING_NULLABLE = (dialect, rs) -> {
		String value = rs.getString(1);
		if (rs.wasNull()) {
			return null;
		}
		return value;
	};
	
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

	
	private static void checkOnlyOneResult(ResultSet rs, Object value) throws SQLException {
		if (rs.next()) {
			throw new ResultSetMapperException(ResultSetMapperException.Type.MORE_THAN_ONE_RESULT, of("value", value));
		}
	}

}
