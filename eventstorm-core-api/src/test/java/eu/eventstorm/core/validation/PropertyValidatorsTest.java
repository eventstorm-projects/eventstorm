package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.ArrayList;

import static eu.eventstorm.core.validation.PropertyValidators.listNotEmpty;
import static eu.eventstorm.core.validation.PropertyValidators.notEmpty;
import static eu.eventstorm.core.validation.PropertyValidators.notNull;
import static eu.eventstorm.core.validation.PropertyValidators.size;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(LoggerInstancePostProcessor.class)
class PropertyValidatorsTest {

	@Test
	void testIsEmpty() {
		ValidatorContext context = new ValidatorContextImpl();

		notEmpty().validate("fake", null, context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		notEmpty().validate("fake", "", context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		notEmpty().validate("fake", "VAL", context);
		assertFalse(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		listNotEmpty().validate("fake", null, context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		listNotEmpty().validate("fake", new ArrayList<>(), context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		listNotEmpty().validate("fake", ImmutableList.of("test"), context);
		assertFalse(context.hasConstraintViolation());
	}
	
	@Test
	void testIsNull() {

		ValidatorContext context = new ValidatorContextImpl();
		notNull().validate("fake", null, context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		notNull().validate("fake", LocalDate.now(), context);
		assertFalse(context.hasConstraintViolation());
		
	}
	
	@Test
	void testSizeForString() {

		ValidatorContext context = new ValidatorContextImpl();
		size(1,4,"X23").validate("property", "FAKEE", context);
		assertTrue(context.hasConstraintViolation());

		context = new ValidatorContextImpl();
		size(1,4,"X23").validate("property", "", context);
		assertTrue(context.hasConstraintViolation());
		
		context = new ValidatorContextImpl();
		size(1,4,"X23").validate("property", "FAKE", context);
		assertFalse(context.hasConstraintViolation());
		
		context = new ValidatorContextImpl();
		size(1,4,"X23").validate("property", "F", context);
		assertFalse(context.hasConstraintViolation());
		
	}
	
	@Test
	void testAnd() {
		ValidatorContext context = new ValidatorContextImpl();
		PropertyValidators.and(notEmpty(),size(1,4,"X23")).validate("property", "FAKEE", context);
		assertTrue(context.hasConstraintViolation());
		
		context = new ValidatorContextImpl();
		PropertyValidators.and(notEmpty(),size(1,4,"X23")).validate("property", null, context);
		assertTrue(context.hasConstraintViolation());
		
		context = new ValidatorContextImpl();
		PropertyValidators.and(notEmpty(),size(1,4,"X23")).validate("property", "GOOD", context);
		assertFalse(context.hasConstraintViolation());
	}

}