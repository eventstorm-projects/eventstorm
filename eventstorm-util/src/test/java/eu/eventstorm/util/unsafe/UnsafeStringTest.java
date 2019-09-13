package eu.eventstorm.util.unsafe;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UnsafeStringTest {

	@Test
	void WellDefinedTest() throws Exception {
		assertUtilClassIsWellDefined(UnsafeString.class);
	}

	@Test
	void valueOfTest() {
		char[] value = new char[] {'H','e','l','l','o',' ','W','o','r','l','d',' ','!','!'};
		String stringValue = UnsafeString.valueOf(value);
		
		assertEquals("Hello World !!", stringValue);
		
		value[0] = 'h';
		assertEquals("hello World !!", stringValue);
		
	}

	@Test
	void getCharsTest() {
		String value = "Hello World !!";
		char[] array = UnsafeString.getChars(value);
		
		array[0] = 'T';
		assertEquals("Tello World !!", value);
	}
	
	
}
