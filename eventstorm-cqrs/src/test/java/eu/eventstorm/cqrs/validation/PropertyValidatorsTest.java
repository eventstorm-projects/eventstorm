	package eu.eventstorm.cqrs.validation;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class PropertyValidatorsTest {

	@Test
	void testIsEmpty() {
		Builder<ConstraintViolation> builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.notEmpty().validate(of("fake"), null, builder);
		assertTrue(builder.build().size() == 1);
		
		builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.notEmpty().validate(of("fake"), "", builder);
		assertTrue(builder.build().size() == 1);
		
		builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.notEmpty().validate(of("fake"), "VAL", builder);
		assertTrue(builder.build().size() == 0);
		
		builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.listNotEmpty().validate(of("fake"), null, builder);
		assertTrue(builder.build().size() == 1);
		
		builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.listNotEmpty().validate(of("fake"), new ArrayList<>(), builder);
		assertTrue(builder.build().size() == 1);
		
		builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.listNotEmpty().validate(of("fake"), ImmutableList.of("test"), builder);
		assertTrue(builder.build().size() == 0);
	}
	
	@Test
	void testIsNull() {
		
		Builder<ConstraintViolation> builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.notNull().validate(of("fake"), null, builder);
		assertTrue(builder.build().size() == 1);
		
		builder = ImmutableList.<ConstraintViolation>builder();
		PropertyValidators.notNull().validate(of("fake"), LocalDate.now(), builder);
		assertTrue(builder.build().size() == 0);
		
	}
}
