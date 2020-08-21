package eu.eventstorm.batch;

import java.io.InputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface BatchResource {

	InputStream getContent();
	
}