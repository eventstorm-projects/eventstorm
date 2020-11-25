package eu.eventstorm.batch;

import java.time.Instant;

import eu.eventstorm.cqrs.batch.BatchJobCreated;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface BatchJobContext {

	void setEndedAt(Instant endedAt);

	void setStatus(BatchStatus status);
	
	void setException(Throwable ex);
	
	BatchJobCreated getBatchJobCreated();
	
	BatchResource getResource(String uuid);

	

}