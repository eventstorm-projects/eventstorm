package eu.eventstorm.core.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Command;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ValidatorsTest {

	@Test
	void testEmpty() {
		assertEquals(ImmutableList.of(), Validators.empty().validate(new Command() {
		}));
	}
}
