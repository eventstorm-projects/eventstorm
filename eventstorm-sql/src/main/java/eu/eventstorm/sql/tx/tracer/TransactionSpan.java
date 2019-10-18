package eu.eventstorm.sql.tx.tracer;

import eu.eventstorm.sql.EventstormSqlException;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionSpan extends AutoCloseable {

	@Override
	void close();

    void exception(EventstormSqlException cause);

    void exception(SQLException cause);

	void tag(String key, String value);

}
