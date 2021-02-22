package eu.eventstorm.cqrs.ex001.validator;

import eu.eventstorm.core.validation.ConstraintViolations;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.core.validation.ValidatorContext;

public class MailPropertyValidator implements PropertyValidator<String> {

	@Override
	public void validate(String property, String value, ValidatorContext context) {
		
		if (!"jm@mail.org".equals(value)) {
			 context.add(ConstraintViolations.ofProperty(property, value, "invalid mail"));
		}
		
	}

}
