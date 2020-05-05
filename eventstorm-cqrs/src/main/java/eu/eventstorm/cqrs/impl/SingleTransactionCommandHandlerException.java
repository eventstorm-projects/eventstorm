package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandException;

@SuppressWarnings("serial")
public final class SingleTransactionCommandHandlerException extends CommandException {

	SingleTransactionCommandHandlerException(Command command, String message, Throwable cause, ImmutableMap<String, Object> parameters) {
		super(command, message, cause, parameters);
	}

}