package eu.eventstorm.util;

import java.io.OutputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class FastByteArrayOutputStream extends OutputStream {

	private static final int TWO = 2;
	/**
	 * Buffer.
	 */
	private byte[] hb;
	/**
	 * Buffer position;
	 */
	private int pos;

	/**
	 * Constructs a stream with the given initial size.
	 */
	public FastByteArrayOutputStream(int initSize) {
		this.pos = 0;
		this.hb = new byte[initSize];
	}

	/**
	 * Ensures that we have a large enough buffer for the given size.
	 */
	private void verifyBufferSize(int sz) {
		if (sz > this.hb.length) {
			byte[] old = this.hb ;
			this.hb  = new byte[Math.max(sz, this.hb.length << TWO)];
			System.arraycopy(old, 0, this.hb, 0, old.length);
		}
	}

	/**
	 * Creates a newly allocated byte array. Its size is the current size of this output stream and the valid contents of the buffer have been copied
	 * into it.
	 * 
	 * @return the current contents of this output stream, as a byte array.
	 */
	public byte[] getByteArray() {
		byte[] buf = new byte[this.pos];
		System.arraycopy(this.hb, 0, buf, 0, this.pos);
		return buf;
	}

	/** {@inheritDoc} */
	@Override
	public void write(int b) {
		verifyBufferSize(this.pos + 1);
		this.hb[this.pos] = (byte) b;
		pos++;
	}

	/** {@inheritDoc} */
	@Override
	public void write(byte[] b) {
		verifyBufferSize(this.pos + b.length);
		System.arraycopy(b, 0, this.hb, this.pos, b.length);
		this.pos += b.length;
	}

	/** {@inheritDoc} */
	@Override
	public void write(byte[] b, int off, int len) {
		verifyBufferSize(this.pos + len);
		System.arraycopy(b, off, this.hb, this.pos, len);
		this.pos += len;
	}

	/**
	 * @return the current size (position) of the buffer.
	 */
	public int size() {
		return this.pos;
	}

	public void reset() {
		this.pos = 0;
	}
	
	/** {@inheritDoc} */
	@Override
	public void close() {
		// nothing to do -> heap space
	}

	public FastByteArrayInputStream toInputStream() {
		return new FastByteArrayInputStream(this.hb, this.pos);
	}

}