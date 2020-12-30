package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.test.Tests;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class StreamsTest {

	@Test
	void testConstructor() throws Exception {
		Tests.assertUtilClassIsWellDefined(Streams.class);
	}
	
	@Test
	void testCopyToByteArray() throws IOException {
		
		assertEquals(0, Streams.copyToByteArray(null).length);
		
		InputStream inputStream = spy(new ByteArrayInputStream("Hello World !".getBytes()));
		byte[] actual = Streams.copyToByteArray(inputStream);
		Assertions.assertArrayEquals("Hello World !".getBytes(), actual);
		verify(inputStream, never()).close();
		
	}

	@Test
	void testCopyStream() throws IOException {

		FastByteArrayInputStream in = new FastByteArrayInputStream("Hello World !!!".getBytes());
		FastByteArrayOutputStream os = new FastByteArrayOutputStream(4);

		Streams.copy(in, os);
		Assertions.assertEquals("Hello World !!!", new String(os.getByteArray()));
	}

	@Test
	void testCopyReaderWriter() throws IOException {

		Reader reader = new StringReader("Hello World !!!");
		Writer writer = new StringWriter();

		Streams.copy(reader, writer);
		Assertions.assertEquals("Hello World !!!", writer.toString());
	}
}
