package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.M3SqlException;

@SuppressWarnings("serial")
public final class DatabaseConfigurationException extends M3SqlException {

    public DatabaseConfigurationException(String message) {
        super(message);
    }

}
