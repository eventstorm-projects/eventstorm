package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

		assertEquals(10, blob.length());
		try (InputStream is = blob.getBinaryStream()) {
			byte[] content = new byte[10];
			assertEquals(10, is.read(content));
			assertEquals("helloWorld", new String(content));
			assertEquals(-1, is.read());
		}
	}

	@Test
	void testClob() throws Exception {
		Clob clob = Lobs.newClob("helloWorld");

		assertEquals(10, clob.length());
		try (Reader reader = clob.getCharacterStream()) {
			char[] content = new char[10];
			assertEquals(10, reader.read(content));
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
			assertEquals(76, is.read(content));
			assertEquals("This is a test with a long string, This is a test with a second long string.", new String(content));
			assertEquals(-1, is.read());
		}
	}

	@Test
	void testInvalidBlobMethods() {
		Blob blob = Lobs.newBlob("helloWorld".getBytes());
		assertThrows(UnsupportedOperationException.class, () -> blob.getBinaryStream(0, 0));
		assertThrows(UnsupportedOperationException.class, () -> blob.truncate(0));
		assertThrows(UnsupportedOperationException.class, () -> blob.getBytes(0, 0));
		assertThrows(UnsupportedOperationException.class, () -> blob.position((byte[]) null, 0));
		assertThrows(UnsupportedOperationException.class, () -> blob.position((Blob) null, 0));
		assertThrows(UnsupportedOperationException.class, () -> blob.setBytes(0, null));
		assertThrows(UnsupportedOperationException.class, () -> blob.setBytes(0, null, 0, 0));
		assertThrows(UnsupportedOperationException.class, () -> blob.setBinaryStream(0));
	}

	@Test
	void testInvalidClobMethods() {
		Clob clob = Lobs.newClob("helloWorld");
		assertThrows(UnsupportedOperationException.class, () -> clob.getCharacterStream(0, 0));
		assertThrows(UnsupportedOperationException.class, () -> clob.truncate(0));
		assertThrows(UnsupportedOperationException.class, () -> clob.getSubString(0, 0));
		assertThrows(UnsupportedOperationException.class, () -> clob.position((String) null, 0));
		assertThrows(UnsupportedOperationException.class, () -> clob.position((Clob) null, 0));
		assertThrows(UnsupportedOperationException.class, () -> clob.setString(0, null));
		assertThrows(UnsupportedOperationException.class, () -> clob.setString(0, null, 0, 0));
		assertThrows(UnsupportedOperationException.class, () -> clob.setAsciiStream(0));
		assertThrows(UnsupportedOperationException.class, () -> clob.setCharacterStream(0));
	}
}
