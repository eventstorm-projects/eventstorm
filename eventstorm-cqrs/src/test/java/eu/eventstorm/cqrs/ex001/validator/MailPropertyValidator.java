package eu.eventstorm.cqrs.ex001.validator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.cqrs.validation.ConstraintViolationImpl;

public class MailPropertyValidator implements PropertyValidator<String> {

	@Override
	public void validate(ImmutableList<String> properties, String value, Builder<ConstraintViolation> builder) {
		
		if (!"jm@mail.org".equals(value)) {
			builder.add(new ConstraintViolationImpl(properties, "invalid mail"));
		}
		
	}

}
