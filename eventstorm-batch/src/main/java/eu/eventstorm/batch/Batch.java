package eu.eventstorm.batch;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Batch {

	Event push(String stream, String streamId, BatchJobCreated batchJobCreated);
	
}