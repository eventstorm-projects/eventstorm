package eu.eventstorm.cqrs.ex001.validator;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;
import eu.eventstorm.cqrs.validation.PropertyValidators;
import eu.eventstorm.cqrs.validation.Validator;

public final class CreateUserCommandValidator implements Validator<CreateUserCommand>{
	
	@Override
	public ImmutableList<ConstraintViolation> validate(CreateUserCommand command) {
		ImmutableList.Builder<ConstraintViolation> builder =ImmutableList.builder();
		PropertyValidators.notEmpty().validate(ImmutableList.of("name"), command.getName(), builder);
		return builder.build();
	}

}
