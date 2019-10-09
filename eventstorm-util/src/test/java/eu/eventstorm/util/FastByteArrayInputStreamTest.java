package eu.eventstorm.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class FastByteArrayInputStreamTest {

	@Test
	void testRead() {

		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello".getBytes())) {
			int value = is.read();
			assertEquals((int) 'h', value);
			value = is.read();
			assertEquals((int) 'e', value);
			value = is.read();
			assertEquals((int) 'l', value);
			value = is.read();
			assertEquals((int) 'l', value);
			value = is.read();
			assertEquals((int) 'o', value);

			assertEquals(5, is.pos());
			
			value = is.read();
			assertEquals(-1, value);
			
			assertEquals(5, is.pos());
			
			value = is.read();
			assertEquals(-1, value);
			
			assertEquals(5, is.pos());
			assertEquals(5, is.size());
		}
		
		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello".getBytes())) {
			assertArrayEquals("hello".getBytes(),is.readAll());
		}
	}

	@Test
	void testReadByteArray() {

		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello World !!".getBytes())) {
			byte[] bytes = new byte[6];

			is.read(bytes);
			assertEquals("hello ", new String(bytes));

			is.read(bytes);
			assertEquals("World ", new String(bytes));

			int value = is.read(bytes);
			assertEquals("!!", new String(bytes, 0, value));
			assertEquals(2, value);

			assertEquals(-1, is.read(bytes));
			assertEquals(-1, is.read(bytes));
			assertEquals(-1, is.read());
			assertEquals(-1, is.read(bytes, 0, 4));
		}
	}

	@Test
	void testReadByteArrayReset() {

		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello World !!".getBytes())) {
			byte[] bytes = new byte[8];

			is.read(bytes);
			assertEquals("hello Wo", new String(bytes));

			is.reset();
			is.read(bytes);
			assertEquals("hello Wo", new String(bytes));
			
			
		}
	}
}
