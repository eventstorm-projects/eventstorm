package eu.eventstorm.sql.type.common;

import java.io.InputStream;

import eu.eventstorm.util.FastByteArrayInputStream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DefaultBlob extends AbstractBlob {

	static final byte[] EMPTY = new byte[0];

	private byte[] buf;

	public DefaultBlob(byte[] buf) {
		if (buf == null) {
			this.buf = EMPTY;
		} else {
			this.buf = buf.clone();
		}
	}

	protected final void setBuf(byte[] buf) {
		this.buf = buf;
    }

    protected final byte[] getBuf() {
		return this.buf;
    }

	@Override
	public final long length() {
		return buf.length;
	}

	@Override
	public final InputStream getBinaryStream() {
		return new FastByteArrayInputStream(this.buf);
	}

}
