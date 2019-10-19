package eu.eventstorm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nullable;

public final class Streams {
	
	private static final byte[] EMPTY = new byte[0];
	
	public static final int BUFFER_SIZE = 4096;
	
	private Streams() {
	}

	/**
	 * Copy the contents of the given InputStream into a new byte array.
	 * Leaves the stream open when done.
	 */
	public static byte[] copyToByteArray(@Nullable InputStream in) throws IOException {
		if (in == null) {
			return EMPTY;
		}

		FastByteArrayOutputStream out = new FastByteArrayOutputStream(BUFFER_SIZE);
		copy(in, out);
		return out.getByteArray();
	}
	
	public static int copy(InputStream in, OutputStream out) throws IOException {
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}
	
}
