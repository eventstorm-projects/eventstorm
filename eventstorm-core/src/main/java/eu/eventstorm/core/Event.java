package eu.eventstorm.core;

import java.time.OffsetDateTime;

import eu.eventstorm.util.ToStringBuilder;

public class Event<T extends EventData> {

	private final AggregateId aggregateId;
	
	private final String aggreateType;
	
	private final int version;
	
	private final T eventData;

	private final OffsetDateTime timestamp;
	
	public Event(AggregateId aggregateId, String aggreateType, OffsetDateTime timestamp, int version, T eventData) {
		this.aggregateId = aggregateId;
		this.aggreateType = aggreateType;
		this.timestamp = timestamp;
		this.version = version;
		this.eventData = eventData;
	}

	public AggregateId getAggregateId() {
		return aggregateId;
	}

	public String getAggreateType() {
		return aggreateType;
	}

	public int getVersion() {
		return version;
	}

	public T getEventData() {
		return eventData;
	}

	public OffsetDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("aggregateId",aggregateId )
				.append("aggreateType", aggreateType)
				.append("version", version)
				.append("timestamp", timestamp)
				.append("eventData", eventData)
				.toString();
	}
	
}
