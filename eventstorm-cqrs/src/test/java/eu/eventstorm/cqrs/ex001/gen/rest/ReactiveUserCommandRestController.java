package eu.eventstorm.cqrs.ex001.gen.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.impl.ReactiveCommandContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public final class ReactiveUserCommandRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveUserCommandRestController.class);

	private final CommandGateway gateway;

	public ReactiveUserCommandRestController(CommandGateway gateway) {
		this.gateway = gateway;
	}

	@PostMapping(name = "reactive/user/create")
	public Flux<Event> createUserCommand(ServerWebExchange exchange, CreateUserCommand command) {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("createUserCommand (reactive/user/create) : [{}]", command);
		}
		
		return Mono.empty().flatMapMany(emtpy -> gateway.dispatch(new ReactiveCommandContext(exchange), command));
	}

}
