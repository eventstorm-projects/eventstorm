package eu.eventstorm.sql.util;

import java.sql.Timestamp;
import java.sql.Date;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Dates {

	private Dates() {
	}
	
	public static Timestamp convertTimestamp(String timestamp) {
		return Timestamp.from(eu.eventstorm.util.Dates.parseOffsetDateTime(timestamp).toInstant());
	}

	public static Date convertDate(String date) {
		return Date.valueOf(eu.eventstorm.util.Dates.parseLocalDate(date));
	}
}
