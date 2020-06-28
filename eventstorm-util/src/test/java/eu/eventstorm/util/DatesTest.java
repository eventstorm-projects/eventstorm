package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DatesTest {

	@Test
	void testBadFormat() {
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime(null));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime(""));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("A"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("202A"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2020T"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35W"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35Z1"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35:44Z1"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35:44;Z1"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35-00:00"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35:00"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35:00W"));
		assertThrows(DateTimeException.class, () -> Dates.parseOffsetDateTime("2011-03-11T18:35:00.1"));
	}
	
	@Test
	void testOffsetDateTime() {
		OffsetDateTime odt = Dates.parseOffsetDateTime("2011-03-11T18:35Z");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(00, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.UTC, odt.getOffset());
		
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35+02:00");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(00, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.ofHoursMinutes(2, 0), odt.getOffset());
		
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35-11:30");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(00, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.ofHoursMinutes(-11, -30), odt.getOffset());
		
		Dates.parseOffsetDateTime("2011-03-11T18:35+00:00");
		Dates.parseOffsetDateTime("2011-03-11T18:35+00:30");
		Dates.parseOffsetDateTime("2011-03-11T18:35+01:45");
		Dates.parseOffsetDateTime("2011-03-11T18:35-00:30");
	}
	
	@Test
	void testOffsetDateTimeSecond() {
		OffsetDateTime odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23Z");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.UTC, odt.getOffset());
		
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23+04:15");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(ZoneOffset.ofHoursMinutes(4, 15), odt.getOffset());
		
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23-04:15");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(ZoneOffset.ofHoursMinutes(-4, -15), odt.getOffset());
		
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.1Z");
		assertEquals(100000000, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.12Z");
		assertEquals(120000000, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.123Z");
		assertEquals(123000000, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.1234Z");
		assertEquals(123400000, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.12345Z");
		assertEquals(123450000, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.123456Z");
		assertEquals(123456000, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.1234567Z");
		assertEquals(123456700, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.12345678Z");
		assertEquals(123456780, odt.getNano());
		odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.123456789Z");
		assertEquals(123456789, odt.getNano());
	}
	
	@Test
	void testOffsetDateTimeSecondMili() {
		OffsetDateTime odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.123Z");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(123_000_000, odt.getNano());
		assertEquals(ZoneOffset.UTC, odt.getOffset());
	}
	
	@Test
	void testLocalDate() {
		LocalDate ld = Dates.parseLocalDate("2011-03-11");
		assertEquals(2011, ld.getYear());
		assertEquals(03, ld.getMonthValue());
		assertEquals(11, ld.getDayOfMonth());
		
		
		assertThrows(DateTimeException.class, () -> Dates.parseLocalDate(null));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalDate(""));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalDate("A"));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalDate("201A-01-02"));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalDate("2020T01-02"));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalDate("2020-01T02"));
		
	}
	
	@Test
	void testLocalTime() {
		LocalTime lt = Dates.parseLocalTime("18:35:20");
		assertEquals(18, lt.getHour());
		assertEquals(35, lt.getMinute());
		assertEquals(20, lt.getSecond());
		
		
		assertThrows(DateTimeException.class, () -> Dates.parseLocalTime(null));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalTime(""));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalTime("A"));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalTime("12:23:2A"));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalTime("1A:12:12"));
		assertThrows(DateTimeException.class, () -> Dates.parseLocalTime("12:A1:12"));
		
	}
	
	@Test
	void testFormatOffsetDateTime() {
		OffsetDateTime odt = Dates.parseOffsetDateTime("2011-03-11T18:35:23.123Z");
		assertEquals("2011-03-11T18:35:23.123Z", Dates.format(odt));
		assertNull(Dates.format(null));
	}
}