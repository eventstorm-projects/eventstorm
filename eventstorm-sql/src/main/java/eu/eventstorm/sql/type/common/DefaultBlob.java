package eu.eventstorm.sql.type.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;

import eu.eventstorm.util.FastByteArrayInputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultBlob implements Blob {

	private static final byte[] EMTPY = new byte[0]; 
	
	private byte[] buf;
	
	public DefaultBlob(byte[] buf) {
		if (buf == null) {
			this.buf = EMTPY;
		} else {
			this.buf = buf.clone();	
		}
	}
	
	protected final void setBuf(byte[] buf) {
		this.buf = buf;
	}

	@Override
	public final long length() {
		return buf.length;
	}

	@Override
	public byte[] getBytes(long pos, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final InputStream getBinaryStream() {
		return new FastByteArrayInputStream(this.buf);
	}

	@Override
	public long position(byte[] pattern, long start) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long position(Blob pattern, long start) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setBytes(long pos, byte[] bytes) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setBytes(long pos, byte[] bytes, int offset, int len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream setBinaryStream(long pos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void truncate(long len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void free() {		
	}

	@Override
	public InputStream getBinaryStream(long pos, long length) {
		throw new UnsupportedOperationException();
	}

}
