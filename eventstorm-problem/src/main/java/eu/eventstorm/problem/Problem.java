package eu.eventstorm.problem;

import java.net.URI;
import java.time.OffsetDateTime;

import com.google.common.collect.ImmutableMap;

/**
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807: Problem Details for HTTP APIs</a>
 * 
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Problem {

	static ProblemBuilder builder() {
        return new ProblemBuilder();
    }

	int getStatus();
	
	URI getType();
	
	String getInstance();

	String getTitle();
	
	String getDetail();

	ImmutableMap<String, Object> getParams();

	String getTraceId();
	
	OffsetDateTime getTimestamp();
	
}
