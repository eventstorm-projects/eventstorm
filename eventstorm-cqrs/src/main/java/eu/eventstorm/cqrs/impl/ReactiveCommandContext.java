package eu.eventstorm.cqrs.impl;

import java.util.Collections;

import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;

import eu.eventstorm.cqrs.CommandContext;

public class ReactiveCommandContext implements CommandContext {

	private final ServerWebExchange exchange;
	
	public ReactiveCommandContext(ServerWebExchange exchange) {
		this.exchange = exchange;
	}
	
	public String getPathVariable(String name) {
		String attributeName = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
		return String.valueOf(exchange.getAttributeOrDefault(attributeName, Collections.emptyMap()).get(name));	
	}

}
