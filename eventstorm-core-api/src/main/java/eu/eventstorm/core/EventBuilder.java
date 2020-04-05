package eu.eventstorm.core;

import java.time.OffsetDateTime;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventBuilder<T extends EventPayload> {

    private StreamId streamId;
    private String stream;
    private int revision;
    private T payload;
    private OffsetDateTime timestamp;

    public Event<T> build() {
        return new EventImpl<>(streamId, stream, timestamp, revision, payload);
    }

    public EventBuilder<T> withStreamId(StreamId streamId) {
        this.streamId = streamId;
        return this;
    }
    
    public EventBuilder<T> withStream(String stream) {
        this.stream = stream;
        return this;
    }
    
    public EventBuilder<T> withRevision(int revision) {
        this.revision = revision;
        return this;
    }
    
    public EventBuilder<T> withPayload(T payload) {
        this.payload = payload;
        return this;
    }
    
    public EventBuilder<T> withTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    
}
