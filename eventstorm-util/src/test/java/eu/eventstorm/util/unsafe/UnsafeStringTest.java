package eu.eventstorm.util.unsafe;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;
import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.eventstorm.util.Jvm;
import org.junit.jupiter.api.Test;

class UnsafeStringTest {

	@Test
	void WellDefinedTest() throws Exception {
		assertUtilClassIsWellDefined(UnsafeString.class);
	}

	@Test
	void valueOfTest() {
		if (Jvm.isJava8()) {
			char[] value = new char[] {'H','e','l','l','o',' ','W','o','r','l','d',' ','!','!'};
			String stringValue = UnsafeString.valueOf(value);

			assertEquals("Hello World !!", stringValue);

			value[0] = 'h';
			assertEquals("hello World !!", stringValue);
		} else {
			byte[] value = new byte[] {'H','e','l','l','o',' ','W','o','r','l','d',' ','!','!'};
			String stringValue = UnsafeString.valueOf(value);

			assertEquals("Hello World !!", stringValue);

			value[0] = 'h';
			assertEquals("hello World !!", stringValue);
		}
	}

	@Test
	void getCharsTest() {
		if (Jvm.isJava8()) {
			String value = "Hello World !!";
			char[] array = UnsafeString.getChars(value);

			array[0] = 'T';
			assertEquals("Tello World !!", value);
		} else {
			String value = "Hello World !!";
			byte[] array = UnsafeString.getBytes(value);

			array[0] = 'T';
			assertEquals("Tello World !!", value);
		}

	}
	
	
}
