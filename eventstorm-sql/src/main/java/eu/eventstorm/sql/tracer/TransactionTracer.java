package eu.eventstorm.sql.tracer;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.impl.Transaction;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionTracer {

	TransactionSpan begin(Transaction transaction);
	
	TransactionSpan span(String name);

	PreparedStatement decorate(PreparedStatement prepareStatement);
	
}