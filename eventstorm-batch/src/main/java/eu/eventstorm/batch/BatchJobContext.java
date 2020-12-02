package eu.eventstorm.batch;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import eu.eventstorm.cqrs.batch.BatchJobCreated;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface BatchJobContext {

	void setEndedAt(Instant endedAt);

	void setStatus(BatchStatus status);
	
	void setException(Throwable ex);
	
	void log(String key, String value);
	
	void log(String key, Map<String, Object> value);
	
	void log(String key, List<Object> value);
	
	BatchJobCreated getBatchJobCreated();
	
	BatchResource getResource(String uuid);

	

}