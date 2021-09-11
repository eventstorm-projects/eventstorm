package eu.eventstorm.sql.tracer;

import java.sql.PreparedStatement;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.impl.TransactionQueryContext;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionTracer {

	TransactionSpan begin(Transaction transaction);
	
	TransactionSpan span(String name);

	PreparedStatement decorate(PreparedStatement prepareStatement);

	TransactionQueryContext newTransactionContext(PreparedStatement ps, SqlQuery query);
}