package eu.eventstorm.batch;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public enum BatchStatus {

	// The job has been submitted to the batch runtime.
	STARTING,

	// The job is running.
	STARTED,

	// The job has been requested to stop.
	STOPPING,

	// The job has stopped.
	STOPPED,

	// The job finished executing because of an error.
	FAILED,
	
    // The job finished executing successfully.
	COMPLETED,
	
    // The job was marked abandoned.
	ABANDONED;
	
}
