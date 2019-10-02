package eu.eventstorm.sql.id;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class IdentifierException extends EventstormSqlException {

	public enum Type implements EventstormSqlExceptionType {
		SEQUENCE_EXECUTE_QUERY, SEQUENCE_RESULT_SET_NEXT, SEQUENCE_EXTRACT, SEQUENCE_NO_RESULT
	}
	
    public IdentifierException(Type type, ImmutableMap<String, Object> values, Exception exception) {
        super(type, values, exception);
    }
    
    public IdentifierException(Type type, ImmutableMap<String, Object> values) {
        super(type, values);
    }

}