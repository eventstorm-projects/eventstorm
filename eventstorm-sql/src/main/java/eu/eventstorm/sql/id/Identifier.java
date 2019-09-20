package eu.eventstorm.sql.id;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Identifier<T> {

    T next() throws EventstormSqlException;
}
