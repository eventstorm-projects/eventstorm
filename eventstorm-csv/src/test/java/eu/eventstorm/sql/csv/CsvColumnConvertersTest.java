package eu.eventstorm.sql.csv;

import static eu.eventstorm.sql.csv.CsvColumnConverters.RAW_INTEGER;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CsvColumnConvertersTest {

	@Test
	void testConververInteger() {
		assertNull(RAW_INTEGER.apply("".getBytes()));
	}
}
