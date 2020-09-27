package eu.eventstorm.core;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventstormExceptionType {

	/**
	 * The code for this error (to manage i18n errors) 
	 */
	default String getCode() {
		return Strings.EMPTY;
	}
	
}