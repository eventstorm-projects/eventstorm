package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AggregateIdExtractors {

    private AggregateIdExtractors() {
    }

    public static int extractInteger(AggregateId aggregateId) {
        if (aggregateId instanceof IntegerAggregateId) {
            return ((IntegerAggregateId) aggregateId).getId();
        }
        throw new IllegalStateException();
    }

    public static long extractLong(AggregateId aggregateId) {
        if (aggregateId instanceof LongAggregateId) {
            return ((LongAggregateId) aggregateId).getId();
        }
        throw new IllegalStateException();
    }

}
