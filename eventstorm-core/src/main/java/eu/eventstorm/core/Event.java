package eu.eventstorm.core;

import java.time.OffsetDateTime;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class Event {

	private final String id;
	private final OffsetDateTime timestamp;
	private final String type;
	private final String contentType;
	
	public Event(String id, OffsetDateTime timestamp, String type, String contentType) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.type = type;
		this.contentType = contentType;
	}
	
	
}
