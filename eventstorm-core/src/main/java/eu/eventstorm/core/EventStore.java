package eu.eventstorm.core;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventStore {

	EventStream load(AggregateId aggregateId);
	
	void store(AggregateId id, Event event);

}