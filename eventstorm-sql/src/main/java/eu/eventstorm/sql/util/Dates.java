package eu.eventstorm.sql.util;

import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Dates {

	private Dates() {
	}
	
	public static Timestamp convertTimestamp(String timestamp) {
		return Timestamp.valueOf(eu.eventstorm.util.Dates.parseOffsetDateTime(timestamp).toLocalDateTime());
	}
}
