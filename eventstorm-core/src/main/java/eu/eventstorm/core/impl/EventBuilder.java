package eu.eventstorm.core.impl;

import java.time.OffsetDateTime;


import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventBuilder {

    private String specVersion;
    private AggregateId aggregateId;
    private String aggreateType;
    private String subject;
    private int version;
    private EventPayload payload;
    private OffsetDateTime timestamp;

    public Event build() {
    	if (Strings.isEmpty(specVersion)) {
    		this.specVersion = "1.0";
    	}
        return new EventImpl(specVersion, aggregateId, aggreateType, timestamp, version, subject, payload);
    }

    public EventBuilder specVersion(String specVersion) {
        this.specVersion = specVersion;
        return this;
    }
    
    public EventBuilder aggregateId(AggregateId aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }
    
    public EventBuilder aggreateType(String aggreateType) {
        this.aggreateType = aggreateType;
        return this;
    }
    
    public EventBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public EventBuilder version(int version) {
        this.version = version;
        return this;
    }
    
    public EventBuilder payload(EventPayload payload) {
        this.payload = payload;
        return this;
    }
    
    public EventBuilder timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    
}
