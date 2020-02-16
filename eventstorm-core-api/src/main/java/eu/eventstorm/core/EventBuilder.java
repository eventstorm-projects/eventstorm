package eu.eventstorm.core;

import java.time.OffsetDateTime;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventBuilder<T extends EventPayload> {

    private AggregateId aggregateId;
    private String aggreateType;
    private int revision;
    private T payload;
    private OffsetDateTime timestamp;

    public Event<T> build() {
        return new EventImpl<>(aggregateId, aggreateType, timestamp, revision, payload);
    }

    public EventBuilder<T> withAggregateId(AggregateId aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }
    
    public EventBuilder<T> withAggreateType(String aggreateType) {
        this.aggreateType = aggreateType;
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
