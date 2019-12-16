package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AggregateIds {

	private AggregateIds() {
	}
	
	public static AggregateId from(int id) {
		return new IntegerAggregateId(id);
	}
	
	public static AggregateId from(long id) {
        return new LongAggregateId(id);
    }

	public static AggregateId from(String id) {
		return new StringAggregateId(id);
	}
	
	public static AggregateId compose(AggregateId id1, AggregateId id2) {
        return new ComposeAggregateId(id1, id2);
    }
	
}
