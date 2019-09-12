package eu.eventstorm.sql.id;

import eu.eventstorm.sql.M3SqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SequenceGeneratorException extends M3SqlException {

    public SequenceGeneratorException(String message, Exception exception) {
        super(message, exception);
    }
}
