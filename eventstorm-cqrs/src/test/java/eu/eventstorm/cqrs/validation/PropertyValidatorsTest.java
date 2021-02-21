package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LoggerInstancePostProcessor.class)
class PropertyValidatorsTest {

	@Test
	void testIsEmpty() {
		Builder<ConstraintViolation> builder = ImmutableList.builder();
		PropertyValidators.notEmpty().validate("fake", null, builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.notEmpty().validate("fake", "", builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.notEmpty().validate("fake", "VAL", builder);
		assertEquals(0, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.listNotEmpty().validate("fake", null, builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.listNotEmpty().validate("fake", new ArrayList<>(), builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.listNotEmpty().validate("fake", ImmutableList.of("test"), builder);
		assertEquals(0, builder.build().size());
	}
	
	@Test
	void testIsNull() {
		
		Builder<ConstraintViolation> builder = ImmutableList.builder();
		PropertyValidators.notNull().validate("fake", null, builder);
		assertEquals(1, builder.build().size());
		
		builder = ImmutableList.builder();
		PropertyValidators.notNull().validate("fake", LocalDate.now(), builder);
		assertEquals(0, builder.build().size());
		
	}
}
