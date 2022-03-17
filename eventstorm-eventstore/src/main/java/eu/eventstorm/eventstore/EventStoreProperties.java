package eu.eventstorm.eventstore;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eu.eventstorm.eventstore")
public class EventStoreProperties {

	private String eventDataTypeUrl;

	public String getEventDataTypeUrl() {
		return eventDataTypeUrl;
	}

	public void setEventDataTypeUrl(String eventDataTypeUrl) {
		this.eventDataTypeUrl = eventDataTypeUrl;
	}


	
}