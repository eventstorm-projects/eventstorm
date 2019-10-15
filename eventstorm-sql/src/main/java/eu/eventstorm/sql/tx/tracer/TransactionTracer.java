package eu.eventstorm.sql.tx.tracer;

import java.sql.PreparedStatement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionTracer {

	TransactionSpan rollback();

	TransactionSpan commit();

	TransactionSpan close();

	TransactionSpan span();

	PreparedStatement decorate(PreparedStatement prepareStatement);
	
}