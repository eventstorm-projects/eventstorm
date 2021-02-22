package eu.eventstorm.cqrs.validation;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.core.validation.ConstraintViolations.ofNullProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import eu.eventstorm.core.validation.ValidationException;
import eu.eventstorm.core.validation.ValidatorContext;
import eu.eventstorm.core.validation.ValidatorContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
final class ValidatorsTest {

	@Test
	void testEmpty() {
		ValidatorContext validatorContext = new ValidatorContextImpl();
		Validators.empty().validate(validatorContext, new Command() {});
		assertFalse(validatorContext.hasConstraintViolation());
	}

	@Test
	void testNullProperty() {
		ValidatorContext validatorContext = new ValidatorContextImpl();
		validatorContext.add(ofNullProperty("prop01","XYZ"));
		ValidationException ex = new ValidationException(validatorContext);
		System.out.println(ex);
	}
}
