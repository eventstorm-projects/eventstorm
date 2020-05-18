package eu.eventstorm.batch;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Batch {

	ImmutableList<Event> push(ImmutableList<EventCandidate> candidates);
	
	

}