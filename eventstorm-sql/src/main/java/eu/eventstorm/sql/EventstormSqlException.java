package eu.eventstorm.sql;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public class EventstormSqlException extends RuntimeException {

	public EventstormSqlException(String message) {
        super(message);
    }

    public EventstormSqlException(String message, Throwable cause) {
        super(message, cause);
    }

}