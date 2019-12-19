package eu.eventstorm.core.cloudevent;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CloudEvents {

	private CloudEvents() {
	}

	public static Stream<CloudEvent> to(ImmutableList<Event<EventPayload>> events) {
		return to(events.stream());
	}
	
	public static Stream<CloudEvent> to(Stream<Event<EventPayload>> events) {
		return events.map(event -> new CloudEventBuilder()
					.withAggregateType(event.getAggregateType())
					.withAggregateId(event.getAggregateId())
					.withPayload(event.getPayload())
					.withTimestamp(event.getTimestamp())
					.withSubject(event.getPayload().getClass().getName())
					.withVersion(event.getRevision())
					.build());
	}
	
}
