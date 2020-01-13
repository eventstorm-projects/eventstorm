package eu.eventstorm.core.ex001.validator;

import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidator;

public class MailValidatorPredicate implements PropertyValidator<String> {

	@Override
	public void validate(String property, String value, Builder<ConstraintViolation> builder) {
		// TODO Auto-generated method stub
		
	}

}
