package eu.eventstorm.sql.util;

import java.sql.Array;
import java.sql.SQLException;

public final class Arrays {

    private static final Class<?> OBJECT_ARRAY = Object[].class;

    private Arrays() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Array array, Class<T> target) throws SQLException {
        if (array == null || array.getArray() == null) {
            return null;
        }

        if (target.isAssignableFrom(array.getArray().getClass())) {
            return (T) array.getArray();
        }

        // with H2, we have always an Object[] ...
        if (OBJECT_ARRAY.isAssignableFrom(array.getArray().getClass())) {
            Object[] original = (Object[]) array.getArray();
            T copy = (T) java.lang.reflect.Array.newInstance(target.getComponentType(), original.length);
            System.arraycopy(original, 0, copy, 0, original.length);
            return copy;
        }

        throw new IllegalStateException("No supported [" + target + "] for [" + array + "]");
    }

}
