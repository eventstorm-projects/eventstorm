package eu.eventstorm.sql.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DatesTest {

	@Test
	void testConvertTimestamp() {
		Timestamp ts = Dates.convertTimestamp("2020-02-14T12:30:15.125+00:00");
		LocalDateTime dt = ts.toLocalDateTime();
		
		assertEquals(2020, dt.getYear());
		assertEquals(2, dt.getMonthValue());
		assertEquals(14, dt.getDayOfMonth());
		
		assertEquals(13, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(15, dt.getSecond());

	}
}
