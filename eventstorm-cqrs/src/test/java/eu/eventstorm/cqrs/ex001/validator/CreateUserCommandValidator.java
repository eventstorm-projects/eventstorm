package eu.eventstorm.cqrs.ex001.validator;

import eu.eventstorm.core.validation.PropertyValidators;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.core.validation.ValidatorContext;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;

public final class CreateUserCommandValidator implements Validator<CreateUserCommand>{
	
	@Override
	public void validate(ValidatorContext context, CreateUserCommand command) {
		PropertyValidators.notEmpty().validate("name", command.getName(), context);
		new MailPropertyValidator().validate("mail", command.getEmail(), context);
	}

}
