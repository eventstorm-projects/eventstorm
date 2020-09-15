package eu.eventstorm.cqrs.context;

import java.util.Collections;

import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ReactiveCommandContext extends DefaultCommandContext {

	private final ServerWebExchange exchange;
	
	public ReactiveCommandContext(ServerWebExchange exchange) {
		this.exchange = exchange;
	}
	
	public String getPathVariable(String name) {
		String attributeName = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
		return String.valueOf(exchange.getAttributeOrDefault(attributeName, Collections.emptyMap()).get(name));	
	}

	public ServerWebExchange getServerWebExchange() {
		return exchange;
	}

}
