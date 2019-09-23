package eu.eventstorm.sql.tx;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionTracer {

	TransactionSpan rollback();

	TransactionSpan commit();

	TransactionSpan close();

	TransactionSpan span();
	
}