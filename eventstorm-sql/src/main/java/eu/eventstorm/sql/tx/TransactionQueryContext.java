package eu.eventstorm.sql.tx;

import java.sql.PreparedStatement;

public interface TransactionQueryContext extends AutoCloseable {

	void close();

	PreparedStatement getPreparedStatement();
}