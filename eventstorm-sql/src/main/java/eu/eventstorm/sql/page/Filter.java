package eu.eventstorm.sql.page;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Filter {
	
	/**
	 * Filter property
	 */
	String getProperty();
	
	/**
	 * Filter operation (ex: [eq] or [gt] or [in] ...
	 * @see grammar
	 */
	String getOperator();
	
	/**
	 * Filter value (raw format without decomposition)
	 */
	String getValue();
	
	/**
	 * Get the evaluator for this Filter  
	 */
	FilterEvaluator getEvalutor();

}