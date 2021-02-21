package eu.eventstorm.cqrs.validation;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.cqrs.validation.ConstraintViolations.ofNullProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
final class ValidatorsTest {

	@Test
	void testEmpty() {
		assertEquals(of(), Validators.empty().validate(null, new Command() {
		}));
	}

	@Test
	void testNullProperty() {

		ValidationException ex = new ValidationException(of(ofNullProperty("prop01","XYZ")));
		System.out.println(ex);
	}
}
