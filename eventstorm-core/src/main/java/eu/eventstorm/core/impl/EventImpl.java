package eu.eventstorm.core.impl;

import java.time.OffsetDateTime;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventImpl<T extends EventPayload> implements Event<T> {

	private final AggregateId aggregateId;

	private final String aggregateType;

	private final OffsetDateTime timestamp;

	private final int revision;

	private final T payload;

	public EventImpl(AggregateId aggregateId, String aggregateType, OffsetDateTime timestamp, int revision, T payload) {
		this.aggregateId = aggregateId;
		this.aggregateType = aggregateType;
		this.timestamp = timestamp;
		this.payload = payload;
		this.revision = revision;
	}

	@Override
	public AggregateId getAggregateId() {
		return aggregateId;
	}

	@Override
	public String getAggregateType() {
		return this.aggregateType;
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
				.append("aggregateId", aggregateId)
				.append("aggreateType", aggregateType)
				.append("revision", revision)
		        .append("timestamp", timestamp)
		        .append("payload", payload)
		        .toString();
		// @formatter:on
	}	
}
