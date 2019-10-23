package eu.eventstorm.sql.jdbc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Batch<T> extends AutoCloseable {

    @Override
    void close();

    void add(T t);

}