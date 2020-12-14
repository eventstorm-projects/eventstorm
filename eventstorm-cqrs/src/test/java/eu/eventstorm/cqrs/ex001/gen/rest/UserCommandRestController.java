package eu.eventstorm.cqrs.ex001.gen.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.context.ReactiveCommandContext;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;

@RestController
public final class UserCommandRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCommandRestController.class);

	private final CommandGateway gateway;

	public UserCommandRestController(CommandGateway gateway) {
		this.gateway = gateway;
	}

	@PostMapping(name = "command/user/create")
	public void createUserCommand(ServerWebExchange request, CreateUserCommand command) {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("createUserCommand (command/user/create) : [{}]", command);
		}
		gateway.dispatch(new ReactiveCommandContext(request), command).collect(ImmutableList.toImmutableList());
	}

}
