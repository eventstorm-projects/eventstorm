package eu.eventstorm.sql.type.common;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import eu.eventstorm.util.FastByteArrayOutputStream;

public final class Blobs {

	private Blobs() {
	}
	
	public static Blob newBlob(byte[] content) {
		return new DefaultBlob(content);
	}
	
	public static Blob newBlob(FastByteArrayOutputStream baos) {
		return new AbstractBlob() {
			@Override
			public InputStream getBinaryStream() {
				return baos.toInputStream();
			}
			@Override
			public long length() {
				return baos.size();
			}
		};
	}
	
}