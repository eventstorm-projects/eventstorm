package eu.eventstorm.sql.csv;

import static eu.eventstorm.sql.csv.CsvColumnConverters.RAW_INTEGER;
import static eu.eventstorm.sql.csv.CsvColumnConverters.RAW_STRING;
import static eu.eventstorm.sql.csv.CsvColumnConverters.RAW_LOCAL_DATE;
import static eu.eventstorm.sql.csv.CsvColumnConverters.localDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

class CsvColumnConvertersTest {

	@Test
	void testConverterInteger() {
		assertNull(RAW_INTEGER.apply("".getBytes()));
		assertEquals(1, RAW_INTEGER.apply("1".getBytes()));

	}

	@Test
	void testConverterString() {
		assertEquals("", RAW_STRING.apply("".getBytes()));
		assertEquals("Hello World", RAW_STRING.apply("Hello World".getBytes()));
	}

	@Test
	void testConverterLocalDate() {
		Function<byte[], LocalDate> converter = localDate(DateTimeFormatter.ISO_LOCAL_DATE);
		assertEquals(LocalDate.of(2011,3,9), converter.apply("2011-03-09".getBytes()));
		assertEquals(LocalDate.of(2011,3,9), RAW_LOCAL_DATE.apply("2011-03-09".getBytes()));
	}
}
