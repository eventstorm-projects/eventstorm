package eu.eventstorm.cqrs.ex001.validator;

import com.google.common.collect.ImmutableList.Builder;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.cqrs.validation.ConstraintViolations;

public class MailPropertyValidator implements PropertyValidator<String> {

	@Override
	public void validate(String property, String value, Builder<ConstraintViolation> builder) {
		
		if (!"jm@mail.org".equals(value)) {
			builder.add(ConstraintViolations.ofProperty(property, value, "invalid mail"));
		}
		
	}

}
