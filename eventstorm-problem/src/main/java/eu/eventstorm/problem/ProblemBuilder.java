package eu.eventstorm.problem;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ProblemBuilder {

	private static final Set<String> RESERVED_PROPERTIES = ImmutableSet.of("type", "title", "status", "trace_id", "detail", "instance");

	private String traceId;
	private OffsetDateTime timestamp;
	private URI type;
    private URI instance;
    private int status;
    private String title;
    private String detail;
    private final Map<String, String> params = new LinkedHashMap<>();
    
    ProblemBuilder() {
	}

    public ProblemBuilder with(HttpServletRequest req) {
    	this.type = URI.create(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath());

    	String originalUri = (String) req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    	
    	if (originalUri != null) {
    		this.instance = URI.create(originalUri);
    	} else{
    		this.instance = URI.create(req.getServletPath() + 
        			(Strings.isEmpty(req.getPathInfo())? "" : req.getPathInfo()) + 
        			(Strings.isEmpty(req.getQueryString())? "" : req.getQueryString()));	
    	}
		return this;
	}
	
    public ProblemBuilder withTraceId(String traceId) {
    	this.traceId = traceId;
    	return this;
    }
    
    public ProblemBuilder withTimestamp(OffsetDateTime timestamp) {
    	this.timestamp = timestamp;
		return this;
	}
    
	public ProblemBuilder withType(URI type) {
		this.type = type;
		return this;
	}
	
	public ProblemBuilder withInstance(URI instance) {
		this.instance = instance;
		return this;
	}

	public ProblemBuilder withStatus(int status) {
		this.status = status;
		return this;
	}
	
	public ProblemBuilder withTitle(String title) {
		this.title = title;
		return this;
	}
	
	public ProblemBuilder withDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public ProblemBuilder with(ImmutableMap<String, Object> map) {
		map.forEach(this::with);
		return this;
	}
	
	public ProblemBuilder with(String key, Object value) {
		
		if (RESERVED_PROPERTIES.contains(key)) {
			return this;
		}
		
		if (value == null) {
			this.params.put(key, "null");
			return this;
		}
		
		if (value instanceof Optional) {
			
			Optional<?> op= (Optional<?>)value;
			if (op.isPresent()) {
				this.params.put(key, op.get().toString());
			} else {
				this.params.put(key, "null");
			}
			return this;			
		}
		
		this.params.put(key, value.toString());
		return this;
	}

	public Problem build() {
		if (Strings.isEmpty(traceId)) {
			this.traceId = Optional.ofNullable(Tracing.currentTracer())
					.map(Tracer::currentSpan)
					.map(Span::context)
					.map(TraceContext::traceIdString)
					.orElse("noTraceId")
					;
		}
		if (this.timestamp == null) {
			this.timestamp = OffsetDateTime.now();
		}
		return new ProblemImpl(traceId, timestamp, type, instance, status, title, detail, ImmutableMap.<String,Object>builder().putAll(params).build());
	}

}
