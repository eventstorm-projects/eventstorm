package eu.eventstorm.sql.builder;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SqlBuilderException extends EventstormSqlException {

	public enum Type implements EventstormSqlExceptionType {
		SELECT, INSERT, UPDATE, DELETE
	}

	public SqlBuilderException(Type type, ImmutableMap<String, Object> values) {
		super(type, values);
	}
	
}