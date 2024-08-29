package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.test.Tests;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class StringsTest {

    @Test
	void testConstructor() throws Exception {
		Tests.assertUtilClassIsWellDefined(Strings.class);
	}

	@Test
	void testIsEmpty() {
		assertTrue(Strings.isEmpty(null));
		assertTrue(Strings.isEmpty(""));
		assertFalse(Strings.isEmpty(" "));
		assertFalse(Strings.isEmpty("bob"));
		assertFalse(Strings.isEmpty("  bob  "));
	}

	@Test
	void testIsNotEmpty() {
		assertFalse(Strings.isNotEmpty(null));
		assertFalse(Strings.isNotEmpty(""));
		assertTrue(Strings.isNotEmpty(" "));
		assertTrue(Strings.isNotEmpty("bob"));
		assertTrue(Strings.isNotEmpty("  bob  "));
	}
}