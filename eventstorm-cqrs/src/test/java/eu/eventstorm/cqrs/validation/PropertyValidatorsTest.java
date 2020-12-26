	package eu.eventstorm.cqrs.validation;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
		Builder<ConstraintViolation> builder = ImmutableList.builder();
		PropertyValidators.notEmpty().validate(of("fake"), null, builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.notEmpty().validate(of("fake"), "", builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.notEmpty().validate(of("fake"), "VAL", builder);
		assertEquals(0, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.listNotEmpty().validate(of("fake"), null, builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.listNotEmpty().validate(of("fake"), new ArrayList<>(), builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.listNotEmpty().validate(of("fake"), ImmutableList.of("test"), builder);
		assertEquals(0, builder.build().size());
	}
	
	@Test
	void testIsNull() {
		
		Builder<ConstraintViolation> builder = ImmutableList.builder();
		PropertyValidators.notNull().validate(of("fake"), null, builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.notNull().validate(of("fake"), LocalDate.now(), builder);
		assertEquals(0, builder.build().size());
		
	}
}
