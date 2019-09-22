package eu.eventstorm.sql;

import java.sql.SQLException;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class EventstormRepositoryException extends EventstormSqlException {

	public enum Type implements EventstormSqlExceptionType {
		PREPARED_STATEMENT_SETTER, INSERT_GENERATED_KEYS, EXECUTE_QUERY, RESULT_SET_NEXT, RESULT_SET_MAPPER
	}

	public EventstormRepositoryException(Type type, SQLException cause) {
		super(type, ImmutableMap.of(), cause);
	}

}
