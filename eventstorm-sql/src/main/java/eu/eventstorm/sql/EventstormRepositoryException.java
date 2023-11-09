package eu.eventstorm.sql;

import java.sql.SQLException;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class EventstormRepositoryException extends EventstormSqlException {

    public static final String PARAM_SQL = "sql";
    public static final String PARAM_POJO = "pojo";
    public static final String PARAM_SIZE = "size";

	public enum Type implements EventstormSqlExceptionType {
		INSERT_GENERATED_KEYS,

		// select
		SELECT_PREPARED_STATEMENT_SETTER, SELECT_EXECUTE_QUERY, SELECT_NEXT, SELECT_MAPPER,
		//stream
		STREAM_PREPARED_STATEMENT_SETTER, STREAM_EXECUTE_QUERY, STREAM_NEXT, STREAM_MAPPER,
	    // insert
		INSERT_MAPPER, INSERT_EXECUTE_QUERY, INSERT_RESULT, INSERT_RETURNING_VALUES,
	    // update
		UPDATE_MAPPER, UPDATE_EXECUTE_QUERY, UPDATE_RESULT,
		// delete
		DELETE_PREPARED_STATEMENT_SETTER, DELETE_EXECUTE_QUERY,
		// batch
		BATCH_ADD, BATCH_EXECUTE_QUERY, BATCH_RESULT,
		// execute query
		PREPARED_STATEMENT_SETTER
		;
	}

	public EventstormRepositoryException(Type type, ImmutableMap<String, Object> params, SQLException cause) {
		super(type, params, cause);
	}

	public EventstormRepositoryException(Type type, ImmutableMap<String, Object> params) {
		super(type, params);
	}

}

