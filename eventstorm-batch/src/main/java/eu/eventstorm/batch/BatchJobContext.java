package eu.eventstorm.batch;

import eu.eventstorm.batch.db.BatchExecution;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface BatchJobContext {

	BatchExecution getBatchExecution();
	
}