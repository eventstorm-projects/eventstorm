package eu.eventstorm.cloudevents;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CloudEventBuilder {

    private String specVersion;
    private String aggregateId;
    private String aggreateType;
    private String subject;
    private int version;
    private Object payload;
    private String timestamp;
    private String dataContentType;

    public CloudEvent build() {
    	if (Strings.isEmpty(specVersion)) {
    		this.specVersion = "1.0";
    	}
        return new CloudEventImpl(specVersion, aggregateId, aggreateType, timestamp, version, subject, dataContentType, payload);
    }

    public CloudEventBuilder withSpecVersion(String specVersion) {
        this.specVersion = specVersion;
        return this;
    }
    
    public CloudEventBuilder withAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
        return this;
    }
    
    public CloudEventBuilder withAggregateType(String aggreateType) {
        this.aggreateType = aggreateType;
        return this;
    }
    
    public CloudEventBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public CloudEventBuilder withVersion(int version) {
        this.version = version;
        return this;
    }
    
    public CloudEventBuilder withPayload(Object payload) {
        this.payload = payload;
        return this;
    }
    
    public CloudEventBuilder withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public CloudEventBuilder withDataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
        return this;
    }
    
    

    
}
