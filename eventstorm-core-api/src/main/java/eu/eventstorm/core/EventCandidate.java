package eu.eventstorm.core;

import com.google.protobuf.AbstractMessage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventCandidate<T extends AbstractMessage> {

	private final String stream;
	private final StreamId streamId;
	private final T message;

	public EventCandidate(String stream, StreamId streamId, T message) {
		this.stream = stream;
		this.streamId = streamId;
		this.message = message;
	}

	public String getStream() {
		return stream;
	}

	public StreamId getStreamId() {
		return streamId;
	}

	public T getMessage() {
		return message;
	}

}