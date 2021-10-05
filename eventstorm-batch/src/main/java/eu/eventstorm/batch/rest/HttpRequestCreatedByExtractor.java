package eu.eventstorm.batch.rest;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface HttpRequestCreatedByExtractor {

	String extract(ServerHttpRequest serverRequest);
	
}