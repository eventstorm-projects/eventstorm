package eu.eventstorm.core.impl;

import java.time.OffsetDateTime;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.InternalEvent;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventImpl implements Event, InternalEvent {

	private final AggregateId aggregateId;

	private final String aggreateType;

	private final int version;

	private final EventPayload payload;

	private final OffsetDateTime timestamp;

	public EventImpl(AggregateId aggregateId, String aggreateType, OffsetDateTime timestamp, int version, EventPayload payload) {
		this.aggregateId = aggregateId;
		this.aggreateType = aggreateType;
		this.timestamp = timestamp;
		this.version = version;
		this.payload = payload;
	}

	@Override
	public String id() {
		return this.aggregateId.toStringValue();
	}

	@Override
	public String source() {
		return null;
	}

	@Override
	public String specVersion() {
		return null;
	}

	@Override
	public String type() {
		return this.aggreateType;
	}

	@Override
	public String dataContentType() {
		return null;
	}

	@Override
	public String dataSchema() {
		return null;
	}

	@Override
	public String subject() {
		return null;
	}

	@Override
	public OffsetDateTime time() {
		return this.timestamp;
	}

	@Override
	public EventPayload data() {
		return this.payload;
	}
	
	@Override
	public AggregateId getAggregateId() {
		return aggregateId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true).append("aggregateId", aggregateId).append("aggreateType", aggreateType).append("version", version)
		        .append("timestamp", timestamp).append("payload", payload).toString();
	}

	@Override
	public InternalEvent internal() {
		return this;
	}

}
