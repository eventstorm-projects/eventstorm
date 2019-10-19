package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

}
