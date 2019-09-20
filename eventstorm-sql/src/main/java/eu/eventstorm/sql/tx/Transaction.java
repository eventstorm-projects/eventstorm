package eu.eventstorm.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

}
