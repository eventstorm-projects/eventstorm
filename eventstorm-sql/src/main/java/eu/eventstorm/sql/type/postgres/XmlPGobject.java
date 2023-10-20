package eu.eventstorm.sql.type.postgres;

import eu.eventstorm.sql.type.Xml;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.unsafe.UnsafeString;
import org.postgresql.util.PGobject;

import java.io.InputStream;
import java.sql.SQLException;

public final class XmlPGobject extends PGobject implements Xml {

    public XmlPGobject(String value) throws SQLException {
        setType("xml");
        setValue(value);
    }

    public XmlPGobject(byte[] value) throws SQLException {
        setType("xml");
        setValue(UnsafeString.valueOf(value));
    }

    @Override
    public InputStream getBinaryStream() {
        return new FastByteArrayInputStream(UnsafeString.getBytes(getValue()));
    }

}
