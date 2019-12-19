package eu.eventstorm.core.cloudevent;

import java.time.OffsetDateTime;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CloudEventImpl implements CloudEvent {

	private final String specVersion;

	private final AggregateId aggregateId;

	private final String aggreateType;

	private final OffsetDateTime timestamp;

	private final String subject;

	private final int version;

	private final EventPayload payload;

	public CloudEventImpl(String specVersion, AggregateId aggregateId, String aggreateType, OffsetDateTime timestamp, int version, String subject,
	        EventPayload payload) {
		this.specVersion = specVersion;
		this.aggregateId = aggregateId;
		this.aggreateType = aggreateType;
		this.timestamp = timestamp;
		this.subject = subject;
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
		return this.specVersion;
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
		return this.subject;
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
	public String toString() {
		// @formatter:off
		return new ToStringBuilder(true)
				.append("specVersion", specVersion)
				.append("aggregateId", aggregateId)
				.append("aggregateType", aggreateType)
				.append("version", version)
		        .append("timestamp", timestamp)
		        .append("subject", subject)
		        .append("payload", payload)
		        .toString();
		// @formatter:on
	}

}
