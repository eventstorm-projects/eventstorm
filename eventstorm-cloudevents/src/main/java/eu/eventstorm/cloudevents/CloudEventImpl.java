package eu.eventstorm.cloudevents;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CloudEventImpl implements CloudEvent {

	private final String specVersion;

	private final String aggregateId;

	private final String aggregateType;

	private final String timestamp;

	private final String subject;

	private final int version;

	private final String dataContentType;
	
	private final Object payload;

	public CloudEventImpl(String specVersion, String aggregateId, String aggregateType, String timestamp, int version, String subject,
			String dataContentType, Object payload) {
		this.specVersion = specVersion;
		this.aggregateId = aggregateId;
		this.aggregateType = aggregateType;
		this.timestamp = timestamp;
		this.subject = subject;
		this.version = version;
		this.payload = payload;
		this.dataContentType = dataContentType;
	}

	@Override
	public String id() {
		return this.aggregateId;
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
		return this.aggregateType;
	}

	@Override
	public String dataContentType() {
		return dataContentType;
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
	public String time() {
		return this.timestamp;
	}

	@Override
	public Object data() {
		return this.payload;
	}

	@Override
	public String toString() {
		// @formatter:off
		return new ToStringBuilder(true)
				.append("specVersion", specVersion)
				.append("aggregateId", aggregateId)
				.append("aggregateType", aggregateType)
				.append("version", version)
		        .append("timestamp", timestamp)
		        .append("subject", subject)
		        .append("payload", payload)
		        .toString();
		// @formatter:on
	}

}
