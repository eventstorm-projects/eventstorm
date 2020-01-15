package eu.eventstorm.core.ex001.validator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidator;

public class MailPropertyValidator implements PropertyValidator<String> {

	@Override
	public void validate(ImmutableList<String> properties, String value, Builder<ConstraintViolation> builder) {
		// TODO Auto-generated method stub
		
	}

}
