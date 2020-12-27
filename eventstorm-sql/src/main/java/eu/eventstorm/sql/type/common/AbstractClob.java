package eu.eventstorm.sql.type.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class AbstractClob implements Clob {

	@Override
	public long length() {
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
	public String getSubString(long pos, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getCharacterStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getAsciiStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long position(String searchstr, long start) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long position(Clob searchstr, long start) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setString(long pos, String str) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setString(long pos, String str, int offset, int len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream setAsciiStream(long pos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Writer setCharacterStream(long pos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader getCharacterStream(long pos, long length) {
		throw new UnsupportedOperationException();
	}


}
