package eu.eventstorm.page;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Filter {
	
	/**
	 * Filter property
	 */
	String getProperty();
	
	/**
	 * Filter operation (ex: [eq] or [gt] or [in] ...*
	 */
	Operator getOperator();
	
	/**
	 * Filter value (raw format without decomposition)
	 */
	String getRaw();

	/**
	 * get decompose values
	 */
	List<String> getValues();

}