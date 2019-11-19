package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AggregateIds {

	private AggregateIds() {
	}
	
	public static AggregateId from(int integer) {
		return new IntegerAggreateId(integer);
	}
	
}