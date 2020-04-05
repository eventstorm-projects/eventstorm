package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface StreamIdGenerator {

	StreamId generate();
	
}
