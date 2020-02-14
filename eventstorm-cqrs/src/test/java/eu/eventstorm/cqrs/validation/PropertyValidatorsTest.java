	package eu.eventstorm.cqrs.validation;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;

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
	}
}
