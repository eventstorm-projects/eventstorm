package eu.eventstorm.sql.type.common;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.SQLException;

public final class Lobs {

	private Lobs() {
	}
	
	public static Clob newClob(String value) {
		return new AbstractClob() {
			@Override
			public Reader getCharacterStream() throws SQLException {
				return new StringReader(value);
			}

			@Override
			public long length() throws SQLException {
				return value.length();
			}
		};
	}
	
}