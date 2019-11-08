package eu.eventstorm.sql.impl;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.EventstormSqlException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionQueryContext extends AutoCloseable {

	void close();

	PreparedStatement preparedStatement();

	<T extends EventstormSqlException> T exception(T exception);
}