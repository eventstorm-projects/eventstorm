package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DatesTest {

	@Test
	void testBadFormat() {
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime(null));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime(""));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("A"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("202A"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2020T"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35W"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35Z1"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35:44Z1"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35:44;Z1"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35-00:00"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35:00"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35:00W"));
		assertThrows(DateTimeException.class, () -> Dates.parseDateTime("2011-03-11T18:35:00.1"));
	}
	
	@Test
	void testOffsetDateTime() {
		OffsetDateTime odt = Dates.parseDateTime("2011-03-11T18:35Z");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(00, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.UTC, odt.getOffset());
		
		odt = Dates.parseDateTime("2011-03-11T18:35+02:00");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(00, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.ofHoursMinutes(2, 0), odt.getOffset());
		
		odt = Dates.parseDateTime("2011-03-11T18:35-11:30");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(00, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.ofHoursMinutes(-11, -30), odt.getOffset());
		
		Dates.parseDateTime("2011-03-11T18:35+00:00");
		Dates.parseDateTime("2011-03-11T18:35+00:30");
		Dates.parseDateTime("2011-03-11T18:35+01:45");
		Dates.parseDateTime("2011-03-11T18:35-00:30");
	}
	
	@Test
	void testOffsetDateTimeSecond() {
		OffsetDateTime odt = Dates.parseDateTime("2011-03-11T18:35:23Z");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(00, odt.getNano());
		assertEquals(ZoneOffset.UTC, odt.getOffset());
		
		odt = Dates.parseDateTime("2011-03-11T18:35:23+04:15");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(ZoneOffset.ofHoursMinutes(4, 15), odt.getOffset());
		
		odt = Dates.parseDateTime("2011-03-11T18:35:23-04:15");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(ZoneOffset.ofHoursMinutes(-4, -15), odt.getOffset());
	}
	
	@Test
	void testOffsetDateTimeSecondMili() {
		OffsetDateTime odt = Dates.parseDateTime("2011-03-11T18:35:23.123Z");
		assertEquals(2011, odt.getYear());
		assertEquals(03, odt.getMonthValue());
		assertEquals(11, odt.getDayOfMonth());
		assertEquals(18, odt.getHour());
		assertEquals(35, odt.getMinute());
		assertEquals(23, odt.getSecond());
		assertEquals(123_000_000, odt.getNano());
		assertEquals(ZoneOffset.UTC, odt.getOffset());
	}
}