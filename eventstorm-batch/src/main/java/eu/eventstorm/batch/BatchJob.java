package eu.eventstorm.batch;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface BatchJob {
	
	void execute(BatchJobContext context);
	
}
