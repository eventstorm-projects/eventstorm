package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.EventstormSqlException;

@SuppressWarnings("serial")
public final class DatabaseConfigurationException extends EventstormSqlException {

    public DatabaseConfigurationException(String message) {
        super(message);
    }

}
