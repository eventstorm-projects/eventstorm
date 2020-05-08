package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class StreamIdExtractors {

    private StreamIdExtractors() {
    }

    public static int extractInteger(StreamId aggregateId) {
        if (aggregateId instanceof IntegerStreamId) {
            return ((IntegerStreamId) aggregateId).getId();
        }
        throw new IllegalStateException();
    }

    public static long extractLong(StreamId aggregateId) {
        if (aggregateId instanceof LongStreamId) {
            return ((LongStreamId) aggregateId).getId();
        }
        throw new IllegalStateException();
    }
    
    public static StreamId extractComposePart1(StreamId aggregateId) {
        if (aggregateId instanceof ComposeStreamId) {
            return ((ComposeStreamId) aggregateId).getId1();
        }
        throw new IllegalStateException();
    }
    
    public static StreamId extractComposePart2(StreamId aggregateId) {
        if (aggregateId instanceof ComposeStreamId) {
            return ((ComposeStreamId) aggregateId).getId2();
        }
        throw new IllegalStateException();
    }

}
