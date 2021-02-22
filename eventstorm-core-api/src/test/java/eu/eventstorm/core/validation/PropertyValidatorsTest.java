package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(LoggerInstancePostProcessor.class)
class PropertyValidatorsTest {

	@Test
	void testIsEmpty() {
		ValidatorContext context = new ValidatorContextImpl();

		PropertyValidators.notEmpty().validate("fake", null, context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		PropertyValidators.notEmpty().validate("fake", "", context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		PropertyValidators.notEmpty().validate("fake", "VAL", context);
		assertFalse(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		PropertyValidators.listNotEmpty().validate("fake", null, context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		PropertyValidators.listNotEmpty().validate("fake", new ArrayList<>(), context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		PropertyValidators.listNotEmpty().validate("fake", ImmutableList.of("test"), context);
		assertFalse(context.hasConstraintViolation());
	}
	
	@Test
	void testIsNull() {

		ValidatorContext context = new ValidatorContextImpl();
		PropertyValidators.notNull().validate("fake", null, context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		PropertyValidators.notNull().validate("fake", LocalDate.now(), context);
		assertFalse(context.hasConstraintViolation());
		
	}

}