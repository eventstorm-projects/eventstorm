package eu.eventstorm.sql.id;

import eu.eventstorm.sql.M3SqlException;

public final class NoIdentifierGenerator<T> implements Identifier<T> {

    @Override
    public T next() throws M3SqlException {
        return null;
    }

}
