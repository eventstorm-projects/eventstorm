package eu.eventstorm.eventstore;

import com.google.protobuf.AbstractMessage;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventCandidate {

	private final String stream;
	private final StreamId streamId;
	private final AbstractMessage message;

	public EventCandidate(String stream, StreamId streamId, AbstractMessage message) {
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

	public AbstractMessage getMessage() {
		return message;
	}

}