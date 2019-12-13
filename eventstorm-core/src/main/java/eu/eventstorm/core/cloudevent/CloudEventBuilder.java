package eu.eventstorm.core.cloudevent;

import java.time.OffsetDateTime;


import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CloudEventBuilder {

    private String specVersion;
    private AggregateId aggregateId;
    private String aggreateType;
    private String subject;
    private int version;
    private EventPayload payload;
    private OffsetDateTime timestamp;

    public CloudEvent build() {
    	if (Strings.isEmpty(specVersion)) {
    		this.specVersion = "1.0";
    	}
        return new CloudEventImpl(specVersion, aggregateId, aggreateType, timestamp, version, subject, payload);
    }

    public CloudEventBuilder specVersion(String specVersion) {
        this.specVersion = specVersion;
        return this;
    }
    
    public CloudEventBuilder aggregateId(AggregateId aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }
    
    public CloudEventBuilder aggreateType(String aggreateType) {
        this.aggreateType = aggreateType;
        return this;
    }
    
    public CloudEventBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public CloudEventBuilder version(int version) {
        this.version = version;
        return this;
    }
    
    public CloudEventBuilder payload(EventPayload payload) {
        this.payload = payload;
        return this;
    }
    
    public CloudEventBuilder timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    
}
