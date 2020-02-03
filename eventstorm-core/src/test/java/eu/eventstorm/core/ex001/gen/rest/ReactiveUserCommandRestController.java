package eu.eventstorm.core.ex001.gen.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.core.CommandGateway;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.ex001.command.CreateUserCommand;
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
	public Flux<Event<EventPayload>> createUserCommand(CreateUserCommand command) {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("createUserCommand (reactive/user/create) : [{}]", command);
		}
		
		return Mono.just(command).flatMapMany(gateway::dispatch);
	}

}