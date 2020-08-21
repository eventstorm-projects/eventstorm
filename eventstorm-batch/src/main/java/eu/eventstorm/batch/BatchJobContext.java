package eu.eventstorm.batch;

import java.time.Instant;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface BatchJobContext {

	void setEndedAt(Instant endedAt);

	void setStatus(BatchStatus status);
	
	List<BatchResource> getResources();

}