package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.sql.Blob;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
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
}
