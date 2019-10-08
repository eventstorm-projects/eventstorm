package eu.eventstorm.util;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class FastByteArrayOutputStreamTest {

	@Test
	void writeShortByteArray() {
		try (FastByteArrayOutputStream os = new FastByteArrayOutputStream(32)) {
			os.write("hello world".getBytes());
			assertEquals("hello world", new String(os.getByteArray(),0,os.size()));
		}
	}
	
	@Test
	void writeBigByteArray() {
		try (FastByteArrayOutputStream os = new FastByteArrayOutputStream(32)) {
			os.write("hello worldhello worldhello worldhello world".getBytes());
			assertEquals("hello worldhello worldhello worldhello world", new String(os.getByteArray(),0,os.size()));	
		}
	}
	
	@Test
	void writeByteArrayPart() {
		try (FastByteArrayOutputStream os = new FastByteArrayOutputStream(32)) {
			os.write("hello worldhello worldhello worldhello world".getBytes(),0,11);
			assertArrayEquals("hello world".getBytes(), os.getByteArray());	
		}
	}
	
	
	@Test
	void writeByteArraywithReallocate() {
		try (FastByteArrayOutputStream os = new FastByteArrayOutputStream(32)) {
			os.write("hello world".getBytes());
			os.write("hello world".getBytes());
			os.write("hello world".getBytes());
			os.write("hello world".getBytes());
			assertArrayEquals("hello worldhello worldhello worldhello world".getBytes(), os.getByteArray());	
		}
	}
	
	@Test
	void writeInt() {
		try (FastByteArrayOutputStream os = new FastByteArrayOutputStream(32)) {
			os.write(48);
			os.write(49);
			os.write(50);
			assertArrayEquals("012".getBytes(), os.getByteArray());	
		}
		
	}
	
	@Test
	void writeRead() throws IOException {
		try (FastByteArrayOutputStream os = new FastByteArrayOutputStream(128)) {
			os.write("hello world !".getBytes());
			assertEquals("hello world !", copyToString(os.toInputStream(), StandardCharsets.UTF_8));
			
			assertEquals(13, os.size());
			os.reset();
			assertEquals(0, os.size());
		}
	}
	
	public static String copyToString(InputStream in, Charset charset) throws IOException {
		if (in == null) {
			return "";
		}

		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, charset);
		char[] buffer = new char[2048];
		int bytesRead = -1;
		while ((bytesRead = reader.read(buffer)) != -1) {
			out.append(buffer, 0, bytesRead);
		}
		return out.toString();
	}
	
}
