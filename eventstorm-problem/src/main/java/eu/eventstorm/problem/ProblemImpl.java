package eu.eventstorm.problem;

import java.net.URI;
import java.time.OffsetDateTime;


import com.google.common.collect.ImmutableMap;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ProblemImpl implements Problem {

	private final OffsetDateTime timestamp;
	private final URI type;
	private final URI instance;
	private final int status;
	private final String title;
	private final String detail;
    private final ImmutableMap<String, Object> params;
    private final String traceId;
	
	ProblemImpl(ProblemBuilder problemBuilder) {
		this.timestamp = problemBuilder.timestamp;
		this.traceId = problemBuilder.traceId;
		this.type = problemBuilder.type;
		this.instance = problemBuilder.instance;
		this.status = problemBuilder.status;
		this.title = problemBuilder.title;
		this.detail = problemBuilder.detail;
		this.params =  ImmutableMap.copyOf(problemBuilder.params);
	}

	@Override
	public URI getType() {
		return type;
	}
	
	@Override
	public URI getInstance() {
		return this.instance;
	}

	@Override
	public String getDetail() {
		return this.detail;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public ImmutableMap<String, Object> getParams() {
		return params;
	}

	@Override
	public String getTraceId() {
		return traceId;
	}
	
	@Override
	public OffsetDateTime getTimestamp() {
		return this.timestamp;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("timestamp", timestamp)
				.append("type", type)
				.append("instance", instance)
				.append("status", status)
				.append("title", title)
				.append("detail", detail)
				.append("params", params)
				.append("traceId", traceId)
				.toString();
	}

}
