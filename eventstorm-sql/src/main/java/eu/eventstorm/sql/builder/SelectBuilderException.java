package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("serial")
public final class SelectBuilderException extends EventstormSqlException{

    public SelectBuilderException(String message) {
        super(message);
    }
}
