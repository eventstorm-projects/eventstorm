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
		INSERT_GENERATED_KEYS, EXECUTE_QUERY, RESULT_SET_NEXT, RESULT_SET_MAPPER,

		// select
		SELECT_PREPARED_STATEMENT_SETTER,
	    // insert
		INSERT_MAPPER, INSERT_EXECUTE_QUERY, INSERT_RESULT,
	    // update
		UPDATE_MAPPER, UPDATE_EXECUTE_QUERY, UPDATE_RESULT,
		// delete
		DELETE_PREPARED_STATEMENT_SETTER, DELETE_EXECUTE_QUERY, 
		// batch
		BATCH_ADD, BATCH_EXECUTE_QUERY, BATCH_RESULT,
		;
	}

	public EventstormRepositoryException(Type type, ImmutableMap<String, Object> params, SQLException cause) {
		super(type, params, cause);
	}

	public EventstormRepositoryException(Type type, ImmutableMap<String, Object> params) {
		super(type, params);
	}

}
