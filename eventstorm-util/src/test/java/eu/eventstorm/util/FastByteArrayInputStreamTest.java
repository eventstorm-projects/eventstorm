package eu.eventstorm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
public class FastByteArrayInputStreamTest {

	@Test
	public void testRead() {

		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello".getBytes())) {
			int value = is.read();
			Assertions.assertEquals((int) 'h', value);
			value = is.read();
			Assertions.assertEquals((int) 'e', value);
			value = is.read();
			Assertions.assertEquals((int) 'l', value);
			value = is.read();
			Assertions.assertEquals((int) 'l', value);
			value = is.read();
			Assertions.assertEquals((int) 'o', value);

			value = is.read();
			Assertions.assertEquals(-1, value);
			value = is.read();
			Assertions.assertEquals(-1, value);

		}
	}

	@Test
	public void testReadByteArray() {

		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello World !!".getBytes())) {
			byte[] bytes = new byte[6];

			is.read(bytes);
			Assertions.assertEquals("hello ", new String(bytes));

			is.read(bytes);
			Assertions.assertEquals("World ", new String(bytes));

			int value = is.read(bytes);
			Assertions.assertEquals("!!", new String(bytes, 0, value));
			Assertions.assertEquals(2, value);

			Assertions.assertEquals(-1, is.read(bytes));
			Assertions.assertEquals(-1, is.read(bytes));
			Assertions.assertEquals(-1, is.read());
			Assertions.assertEquals(-1, is.read(bytes, 0, 4));
		}
	}

	@Test
	public void testReadByteArrayReset() {

		try (FastByteArrayInputStream is = new FastByteArrayInputStream("hello World !!".getBytes())) {
			byte[] bytes = new byte[8];

			is.read(bytes);
			Assertions.assertEquals("hello Wo", new String(bytes));

			is.reset();
			is.read(bytes);
			Assertions.assertEquals("hello Wo", new String(bytes));
			
			
		}
	}
}
