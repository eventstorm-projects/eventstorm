package eu.eventstorm.core.impl;

import java.time.OffsetDateTime;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

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
        return new EventImpl<T>(aggregateId, aggreateType, timestamp, revision, payload);
    }

    public EventBuilder<T> aggregateId(AggregateId aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }
    
    public EventBuilder<T> aggreateType(String aggreateType) {
        this.aggreateType = aggreateType;
        return this;
    }
    
    public EventBuilder<T> revision(int revision) {
        this.revision = revision;
        return this;
    }
    
    public EventBuilder<T> payload(T payload) {
        this.payload = payload;
        return this;
    }
    
    public EventBuilder<T> timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    
}
