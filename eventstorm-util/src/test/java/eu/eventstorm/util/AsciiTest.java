package eu.eventstorm.util;

import static eu.eventstorm.util.Ascii.isDigit;
import static eu.eventstorm.util.Ascii.digit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AsciiTest {

	@Test
	void testIsDigit() {
		assertTrue(isDigit('2'));
		assertFalse(isDigit('\n'));
		assertFalse(isDigit('E'));
	}
	
	@Test
	void testDigit() {
		assertEquals(1, digit('1'));
		assertEquals(2, digit('2'));
	}
	
}
