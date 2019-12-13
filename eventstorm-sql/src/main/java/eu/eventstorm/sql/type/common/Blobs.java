package eu.eventstorm.sql.type.common;

import java.sql.Blob;

public final class Blobs {

	private Blobs() {
	}
	
	public static Blob newBlob(byte[] content) {
		return new DefaultBlob(content);
	}
	
}