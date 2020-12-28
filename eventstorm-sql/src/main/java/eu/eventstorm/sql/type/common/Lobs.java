package eu.eventstorm.sql.type.common;

import eu.eventstorm.util.FastByteArrayOutputStream;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;

public final class Lobs {

	private Lobs() {
	}
	
	public static Clob newClob(String value) {
		return new AbstractClob() {
			@Override
			public Reader getCharacterStream() {
				return new StringReader(value);
			}

			@Override
			public long length() {
				return value.length();
			}
		};
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