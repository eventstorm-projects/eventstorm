package eu.eventstorm.cqrs.ex001.gen.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.impl.ReactiveCommandContext;

@RestController
public final class UserCommandRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCommandRestController.class);

	private final CommandGateway gateway;

	public UserCommandRestController(CommandGateway gateway) {
		this.gateway = gateway;
	}

	@PostMapping(name = "command/user/create")
	public void createUserCommand(ServerWebExchange exchange, CreateUserCommand command) {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("createUserCommand (command/user/create) : [{}]", command);
		}
		gateway.dispatch(new ReactiveCommandContext(exchange), command).collect(ImmutableList.toImmutableList());
	}

}
