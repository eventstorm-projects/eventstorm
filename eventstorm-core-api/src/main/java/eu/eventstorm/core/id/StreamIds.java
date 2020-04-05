package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class StreamIds {

	private StreamIds() {
	}
	
	public static StreamId from(int id) {
		return new IntegerStreamId(id);
	}
	
	public static StreamId from(long id) {
        return new LongStreamId(id);
    }

	public static StreamId from(String id) {
		return new StringStreamId(id);
	}
	
	public static StreamId compose(StreamId id1, StreamId id2) {
        return new ComposeStreamId(id1, id2);
    }
	
}
