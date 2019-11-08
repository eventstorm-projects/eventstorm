package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.type.SqlTypeException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class BlobJsonMapTest {

	@Test
	void testBadJsonMapConstructor() {

		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> new BlobJsonMap("BAD".getBytes()));
		assertEquals(SqlTypeException.Type.READ_JSON, ex.getType());
		assertArrayEquals("BAD".getBytes(), (byte[]) ex.getValues().get(SqlTypeException.PARAM_CONTENT));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testBadJsonMapWrite() {

		BlobJsonMap map = new BlobJsonMap(new HashMap<>());
		
		BadPojo badPojo = new BadPojo();
		map.put("fake", badPojo);
		
		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> map.write());
		assertEquals(SqlTypeException.Type.WRITE_JSON, ex.getType());
		assertEquals(badPojo, (BadPojo) ((Map<String, Object>)ex.getValues().get(SqlTypeException.PARAM_CONTENT_OBJECT)).get("fake"));
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
