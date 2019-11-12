package eu.eventstorm.core;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Event {

	AggregateId getAggregateId();

	int getVersion();

	String getEventType();

	//ImmutableList<String> getDomains();
	
}
