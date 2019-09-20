package eu.eventstorm.sql;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class EventstormSqlException extends RuntimeException {

    /**
	 *  default serial UID.
	 */
	private static final long serialVersionUID = -3772625520039435049L;

	public EventstormSqlException(String message) {
        super(message);
    }

    public EventstormSqlException(Exception exception) {
        super(exception);
    }

    public EventstormSqlException(String message, Exception exception) {
        super(message, exception);
    }

}