package eu.eventstorm.util;

import static eu.eventstorm.util.Ascii.digit;
import static eu.eventstorm.util.Ascii.isDigit;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Dates {

	private static final int RADIX = 10;
	private static final char PLUS = '+';
	private static final char MINUS = '-';
	private static final char DATE_SEPARATOR = '-';
	private static final char SEPARATOR_T= 'T';
	private static final char TIME_SEPARATOR = ':';
	private static final char FRACTION_SEPARATOR = '.';
	private static final char ZULU = 'Z';

	private Dates() {
	}

	public static LocalDate parseLocalDate(String localDate) {
		
		if (Strings.isEmpty(localDate)) {
			throw new DateTimeException("The String is null or empty");
		}
		
		if (localDate.length() != 10) {
			throw new DateTimeException("Invalid LocalDate size (" + localDate.length() + ") [" + localDate + "]");
		}
		
		char[] chars = localDate.toCharArray();

		// separator '-'
        assertCharacter(chars, 4, DATE_SEPARATOR);
	    assertCharacter(chars, 7, DATE_SEPARATOR);

	    return LocalDate.of(parsePositiveInt(chars, 0, 4),
	    		parsePositiveInt(chars, 5, 7), parsePositiveInt(chars, 8, 10));
		
	}
	
	public static LocalTime parseLocalTime(String localTime) {
		
		if (Strings.isEmpty(localTime)) {
			throw new DateTimeException("The String is null or empty");
		}
		
		if (localTime.length() != 8) {
			throw new DateTimeException("Invalid LocalTime size (" + localTime.length() + ") [" + localTime + "]");
		}
		
		char[] chars = localTime.toCharArray();

		// separator ':'
        assertCharacter(chars, 2, TIME_SEPARATOR);
	    assertCharacter(chars, 5, TIME_SEPARATOR);

	    return LocalTime.of(parsePositiveInt(chars, 0, 2),
	    		parsePositiveInt(chars, 3, 5), parsePositiveInt(chars, 6, 8));
	}

	
	public static OffsetDateTime parseOffsetDateTime(String offsetDateTime) {

		if (Strings.isEmpty(offsetDateTime)) {
			throw new DateTimeException("The String is null or empty");
		}
		
		char[] chars = offsetDateTime.toCharArray();

		// year
		int year = parsePositiveInt(chars, 0, 4);

		// separator '-'
		assertCharacter(chars, 4, DATE_SEPARATOR);
		
		// month
		int month = parsePositiveInt(chars, 5, 7);

		// separator '-'
		assertCharacter(chars, 7, DATE_SEPARATOR);
		
		// day
		int day = parsePositiveInt(chars, 8, 10);

		// separator 'T'
		assertCharacter(chars, 10, SEPARATOR_T);

		// hours
		int hours = parsePositiveInt(chars, 11, 13);

		// separator ':'
		assertCharacter(chars, 13, TIME_SEPARATOR);
		
		// minute
		int minutes = parsePositiveInt(chars, 14, 16);

		if (chars[16] == TIME_SEPARATOR) {
			// SECONDS
			return seconds(year, month, day, hours, minutes, chars);
		}

		if (chars[16] == ZULU || chars[16] == PLUS || chars[16] == MINUS) {
			return OffsetDateTime.of(year, month, day, hours, minutes, 0, 0, parseTimeZone(chars, 16));
		}

		throw new DateTimeException("Illegal character [" + chars[16] + "] at position " + (17) + " [" + new String(chars) + "]");

	}

	private static ZoneOffset parseTimeZone(char[] chars, int offset) {
		
		int remain = chars.length - offset;
		
		char sign = chars[offset];
		
		if (sign == ZULU) {
			if (remain != 1) {
				throw new DateTimeException("Found TimeZon 'Z' at position " + (offset + 1) + " but remain " + (remain -1) + "after [" + new String(chars) + "]");
			}
			return ZoneOffset.UTC;
		}
		
		int hours = parsePositiveInt(chars, offset + 1, offset + 3);
		int minutes = parsePositiveInt(chars, offset + 4, offset + 4 + 2);

		if (sign == MINUS && hours == 0 && minutes == 0) {
			throw new DateTimeException("Invalid TimeZone (-00:00)");
		}

		if (sign == MINUS) {
			hours = -hours;
			minutes = -minutes;
		}

		return ZoneOffset.ofHoursMinutes(hours, minutes);
	}

	private static OffsetDateTime seconds(int year, int month, int day, int hour, int minute, char[] chars) {
		int second = parsePositiveInt(chars, 17, 19);

		final int remain = chars.length - 19;

		if (remain == 0) {
			throw new DateTimeException("Missing TimeZone [" + new String(chars) + "]" );
		}

		if (remain == 1) {
			if (chars[19] == ZULU) {
				return OffsetDateTime.of(year, month, day, hour, minute, second, 0, ZoneOffset.UTC);
			} else {
				throw new DateTimeException("Invalid character at position 20 [" + new String(chars) + "]" );
			}
		}

		ZoneOffset offset;
		int fractions = 0;

		if (chars[19] == FRACTION_SEPARATOR) {
			int idx = indexOfNonDigit(chars, 20);
			if (idx != -1) {
				// We have an end of fractions
				final int len = idx - 20;
				fractions = getFractions(chars, idx, len);
				offset = parseTimeZone(chars, idx);
			} else {
				throw new DateTimeException("Missing TimeZone [" + new String(chars) + "]" );
			}
		} else if (chars[19] == PLUS || chars[19] == MINUS) {
			// No fractional sections
			offset = parseTimeZone(chars, 19);
		} else {
			throw new DateTimeException("INVALID");
		}
		return OffsetDateTime.of(year, month, day, hour, minute, second, fractions, offset);
	}

	private static int parsePositiveInt(char[] strNum, int startInclusive, int endExclusive) {
		if (endExclusive > strNum.length) {
			throw new DateTimeException("Unexpected end of expression at position " + strNum.length + " '" + new String(strNum) + "'");
		}

		int result = 0;
		for (int i = startInclusive; i < endExclusive; i++) {
			if (!isDigit(strNum[i])) {
				throw new DateTimeException("Character " + strNum[i] + " is not a digit");
			}
			int digit = digit(strNum[i]);
			result *= RADIX;
			result -= digit;
		}
		return -result;
	}

	public static int indexOfNonDigit(char[] chars, int offset) {
		for (int i = offset; i < chars.length; i++) {
			if (!isDigit(chars[i])) {
				return i;
			}
		}
		return -1;
	}

	private static int getFractions(final char[] chars, final int idx, final int len) {
		final int fractions;
		fractions = parsePositiveInt(chars, 20, idx);
		switch (len) {
		case 1:
			return fractions * 100_000_000;
		case 2:
			return fractions * 10_000_000;
		case 3:
			return fractions * 1_000_000;
		case 4:
			return fractions * 100_000;
		case 5:
			return fractions * 10_000;
		case 6:
			return fractions * 1_000;
		case 7:
			return fractions * 100;
		case 8:
			return fractions * 10;
		default:
			return fractions;
		}
	}

	private static void assertCharacter(char[] chars, int offset, char expected) {
		if (chars[offset] != expected) {
			throw new DateTimeException("Expected character [" + expected + "] at position " + (offset + 1) + " [" + new String(chars) + "]");
		}
	}
}
