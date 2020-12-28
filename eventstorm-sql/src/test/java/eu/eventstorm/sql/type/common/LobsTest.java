package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.util.FastByteArrayOutputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class LobsTest {

	@Test
	void testBlob() throws Exception {
		Blob blob = Lobs.newBlob("helloWorld".getBytes());
		
		try (InputStream is = blob.getBinaryStream()) {
			byte[] content = new byte[10];
			is.read(content);
			assertEquals("helloWorld", new String(content));
			assertEquals(-1, is.read());
		}
	}

	@Test
	void testClob() throws Exception {
		Clob clob = Lobs.newClob("helloWorld");

		try (Reader reader = clob.getCharacterStream()) {
			char[] content = new char[10];
			reader.read(content);
			assertEquals("helloWorld", new String(content));
			assertEquals(-1, reader.read());
		}
	}
	
	@Test
	void testWithInputStream() throws Exception {
		FastByteArrayOutputStream baos = new FastByteArrayOutputStream(16);
		baos.write("This is a test with a long string, This is a test with a second long string.".getBytes());
		Blob blob = Lobs.newBlob(baos);
		
		try (InputStream is = blob.getBinaryStream()) {
			byte[] content = new byte[76];
			is.read(content);
			assertEquals("This is a test with a long string, This is a test with a second long string.", new String(content));
			assertEquals(-1, is.read());
		}
	}

}
