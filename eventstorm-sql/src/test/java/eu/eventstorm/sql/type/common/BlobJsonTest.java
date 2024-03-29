package eu.eventstorm.sql.type.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.json.JacksonJsonMapper;
import eu.eventstorm.sql.type.SqlTypeException;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class BlobJsonTest {

	private JsonMapper mapper = new JacksonJsonMapper();

	@Test
	void badAsMapTest() {

		BlobJson json = new BlobJson(mapper, "BAD".getBytes());

		SqlTypeException ex = assertThrows(SqlTypeException.class, json::asMap);
		assertEquals(SqlTypeException.Type.READ_JSON, ex.getType());
		assertArrayEquals("BAD".getBytes(), (byte[]) ex.getValues().get(SqlTypeException.PARAM_CONTENT));
	}

	@Test
	void badAsListTest() {

		BlobJson json = new BlobJson(mapper, "BAD".getBytes());

		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> json.asList(String.class));
		assertEquals(SqlTypeException.Type.READ_JSON, ex.getType());
		assertArrayEquals("BAD".getBytes(), (byte[]) ex.getValues().get(SqlTypeException.PARAM_CONTENT));

	}

}
