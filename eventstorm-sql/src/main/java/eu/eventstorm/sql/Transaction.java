package eu.eventstorm.sql;

import java.time.Instant;
import java.util.UUID;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Transaction extends AutoCloseable {

    boolean isReadOnly();

    void commit();

    void rollback();

    void close();

    UUID getUuid();

    Instant getStart();

    TransactionDefinition getDefinition();
}
