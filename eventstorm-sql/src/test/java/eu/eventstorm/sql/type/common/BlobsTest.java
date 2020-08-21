package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.sql.Blob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.util.FastByteArrayOutputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class BlobsTest {

	@Test
	void simpleTest() throws Exception {
		Blob blob = Blobs.newBlob("helloWorld".getBytes());
		
		try (InputStream is = blob.getBinaryStream()) {
			byte[] content = new byte[10];
			is.read(content);
			assertEquals("helloWorld", new String(content));
			assertEquals(-1, is.read());
		}
	}
	
	@Test
	void testWithInputStream() throws Exception {
		FastByteArrayOutputStream baos = new FastByteArrayOutputStream(16);
		baos.write("This is a test with a long string, This is a test with a second long string.".getBytes());
		Blob blob = Blobs.newBlob(baos);
		
		try (InputStream is = blob.getBinaryStream()) {
			byte[] content = new byte[76];
			is.read(content);
			assertEquals("This is a test with a long string, This is a test with a second long string.", new String(content));
			assertEquals(-1, is.read());
		}
	}
}
