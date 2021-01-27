package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.EventstormSqlExceptionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlPageRequestDescriptorException extends EventstormSqlException {

	public enum Type implements EventstormSqlExceptionType {
		PROPERTY_NOT_FOUND
	}

	public SqlPageRequestDescriptorException(Type type, ImmutableMap<String, Object> values) {
		super(type, values);
	}
	
}