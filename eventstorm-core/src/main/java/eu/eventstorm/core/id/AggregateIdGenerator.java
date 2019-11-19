package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface AggregateIdGenerator {

	AggregateId generate();
	
}
