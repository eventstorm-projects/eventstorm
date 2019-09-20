package eu.eventstorm.sql.id;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SequenceGeneratorException extends EventstormSqlException {

    public SequenceGeneratorException(String message, Exception exception) {
        super(message, exception);
    }
}
