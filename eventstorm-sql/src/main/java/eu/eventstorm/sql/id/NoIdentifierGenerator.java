package eu.eventstorm.sql.id;

import eu.eventstorm.sql.EventstormSqlException;

public final class NoIdentifierGenerator<T> implements Identifier<T> {

    @Override
    public T next() throws EventstormSqlException {
        return null;
    }

}
