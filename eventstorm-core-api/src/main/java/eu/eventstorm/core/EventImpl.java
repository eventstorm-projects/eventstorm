package eu.eventstorm.core;

import java.time.OffsetDateTime;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventImpl<T extends EventPayload> implements Event<T> {

	private final StreamId streamId;

	private final String stream;

	private final OffsetDateTime timestamp;

	private final int revision;

	private final T payload;

	public EventImpl(StreamId streamId, String stream, OffsetDateTime timestamp, int revision, T payload) {
		this.streamId = streamId;
		this.stream = stream;
		this.timestamp = timestamp;
		this.payload = payload;
		this.revision = revision;
	}

	@Override
	public StreamId getStreamId() {
		return streamId;
	}

	@Override
	public String getStream() {
		return this.stream;
	}

	@Override
	public T getPayload() {
		return this.payload;
	}

	public OffsetDateTime getTimestamp() {
		return timestamp;
	}

	public int getRevision() {
		return revision;
	}

	@Override
	public String toString() {
		// @formatter:off
		return new ToStringBuilder(true)
				.append("streamId", streamId)
				.append("streamI", stream)
				.append("revision", revision)
		        .append("timestamp", timestamp)
		        .append("payload", payload)
		        .toString();
		// @formatter:on
	}	
}
