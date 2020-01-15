package eu.eventstorm.core.ex001.validator;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.ex001.command.CreateUserCommand;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidators;
import eu.eventstorm.core.validation.Validator;

public final class CreateUserCommandValidator implements Validator<CreateUserCommand>{
	
	@Override
	public ImmutableList<ConstraintViolation> validate(CreateUserCommand command) {
		ImmutableList.Builder<ConstraintViolation> builder =ImmutableList.builder();
		PropertyValidators.isEmpty().validate(ImmutableList.of("name"), command.getName(), builder);
		return builder.build();
	}

}
