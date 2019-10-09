package eu.eventstorm.util;

import java.io.InputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class FastByteArrayInputStream extends InputStream {

	/**
	 * The array backing the input stream.
	 */
	private final byte[] hb;

	/**
	 * limit for this buffer.
	 */
	private final int limit;

	/**
	 * Current position in this buffer.
	 */
	private int pos;

	public FastByteArrayInputStream(byte[] hb, int limit) {
		this.hb = hb;
		this.pos = 0;
		this.limit = limit;
	}

	public FastByteArrayInputStream(byte[] hb) {
		this(hb, hb.length);
	}

	/** {@inheritDoc} */
	@Override
	public int read() {
		if (pos == this.limit) {
			return -1;
		}
		return this.hb[this.pos++] & 0xFF;
	}

	/** {@inheritDoc} */
	@Override
	public int read(byte[] b) {
		return read(b, 0, b.length);
	}

	/** {@inheritDoc} */
	@Override
	public int read(byte[] b, int off, int len) {
		if (this.limit == this.pos) {
			return -1;
		}
		if (len < (this.limit - this.pos)) {
			System.arraycopy(this.hb, this.pos, b, off, len);
			this.pos += len;
			return len;
		} else {
			System.arraycopy(this.hb, this.pos, b, off, available());
			int oldPso = this.pos;
			this.pos = this.limit;
			return this.limit - oldPso;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reset() {
		this.pos = 0;
	}

	/** {@inheritDoc} */
	@Override
	public int available() {
		return this.limit - this.pos;
	}
	
	public int pos() {
		return this.pos;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// nothing to do -> heap space
	}

	public byte[] readAll() {
		byte[] bytes = new byte[limit - pos];
		System.arraycopy(this.hb, pos, bytes, 0, limit - pos);
		pos = limit;
		return bytes;
	}
	
	public int size() {
		return this.limit;
	}

}