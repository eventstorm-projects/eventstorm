package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.json.JacksonJsonMapper;
import eu.eventstorm.sql.type.SqlTypeException;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class BlobJsonMapTest {
	
	private JsonMapper mapper = new JacksonJsonMapper();
	
	@Test
	void testJsonMap() {
		
		BlobJsonMap map = new BlobJsonMap(new HashMap<>());
		map.put("test", "value");
		assertEquals("{\"test\":\"value\"}", new String(map.write(mapper)));
		
		Pojo pojo = new Pojo();
		pojo.setValue("Jacques");
		pojo.setAge(39);
		map = new BlobJsonMap(new HashMap<>());
		map.put("key1", pojo);

		assertEquals("{\"key1\":{\"value\":\"Jacques\",\"age\":39}}", new String(map.write(mapper)));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testBadJsonMapWrite() {

		BlobJsonMap map = new BlobJsonMap(new HashMap<>());
		
		BadPojo badPojo = new BadPojo();
		map.put("fake", badPojo);
		
		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> map.write(mapper));
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
