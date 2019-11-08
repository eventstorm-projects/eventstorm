package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class BlobJsonListTest {

	@Test
	void testBadJsonListConstructor() {

		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> new BlobJsonList("BAD".getBytes()));
		assertEquals(SqlTypeException.Type.READ_JSON, ex.getType());
		assertArrayEquals("BAD".getBytes(), (byte[]) ex.getValues().get(SqlTypeException.PARAM_CONTENT));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testBadJsonListWrite() {

		BlobJsonList list = new BlobJsonList(new ArrayList<>());
		
		BadPojo badPojo = new BadPojo();
		list.add(badPojo);
		
		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> list.write());
		assertEquals(SqlTypeException.Type.WRITE_JSON, ex.getType());
		assertEquals(badPojo, (BadPojo) ((List<Object>)ex.getValues().get(SqlTypeException.PARAM_CONTENT_OBJECT)).get(0));
	}

	private static class BadPojo {

		public BadPojo() {
		}

		@SuppressWarnings("unused")
		public int getValue() {
			throw new RuntimeException();
		}
	}

}
