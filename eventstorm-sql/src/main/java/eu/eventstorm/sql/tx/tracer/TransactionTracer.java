package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.tx.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionTracer {

	TransactionSpan begin(Transaction transaction);
	
	TransactionSpan close();

	TransactionSpan span();

	PreparedStatement decorate(String sql, PreparedStatement prepareStatement);
	
}