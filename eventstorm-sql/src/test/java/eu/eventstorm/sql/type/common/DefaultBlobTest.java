package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Blob;

import org.junit.jupiter.api.Test;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DefaultBlobTest {

    @Test
    void testEmptyBlob() {
        DefaultBlob blob = new DefaultBlob(null);
        assertEquals(0, blob.length());
    }

    @Test
    void testUnsupportedOperationException() {
        DefaultBlob blob = new DefaultBlob(null);

        assertThrows(UnsupportedOperationException.class, () -> blob.getBytes(0L, 5));
        assertThrows(UnsupportedOperationException.class, () -> blob.position((Blob)null, 5l));
        assertThrows(UnsupportedOperationException.class, () -> blob.position((byte[])null, 5l));
        assertThrows(UnsupportedOperationException.class, () -> blob.setBytes(0l, (byte[])null));
        assertThrows(UnsupportedOperationException.class, () -> blob.setBytes(0l, (byte[])null, 0, 0));
        assertThrows(UnsupportedOperationException.class, () -> blob.setBinaryStream(0L));
        assertThrows(UnsupportedOperationException.class, () -> blob.truncate(5l));
        assertThrows(UnsupportedOperationException.class, () -> blob.getBinaryStream(5l, 0));


    }


}