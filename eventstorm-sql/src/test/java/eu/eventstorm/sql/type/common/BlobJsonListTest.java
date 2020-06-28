package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

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
class BlobJsonListTest {

	private JsonMapper mapper = new JacksonJsonMapper();

	@SuppressWarnings("unchecked")
	@Test
	void testBadJsonListWrite() {

		BlobJsonList list = new BlobJsonList(new ArrayList<>());
		
		BadPojo badPojo = new BadPojo();
		list.add(badPojo);
		
		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> list.write(mapper));
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
