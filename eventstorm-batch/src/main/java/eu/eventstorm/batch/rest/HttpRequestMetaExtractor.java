package eu.eventstorm.batch.rest;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Map;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface HttpRequestMetaExtractor {

    Map<String, Object> extract(ServerHttpRequest serverRequest);

}