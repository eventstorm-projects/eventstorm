package eu.eventstorm.sql.jdbc;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public class ResultSetMapperException extends EventstormSqlException {

    public ResultSetMapperException(String message) {
        super(message);
    }

    public ResultSetMapperException(String message, Exception exception) {
        super(message, exception);
    }


}
