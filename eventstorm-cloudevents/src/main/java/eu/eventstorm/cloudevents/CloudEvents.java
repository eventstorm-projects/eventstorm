package eu.eventstorm.cloudevents;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CloudEvents {

	private CloudEvents() {
	}

	public static Stream<CloudEvent> to(ImmutableList<Event> events) {
		return to(events.stream());
	}
	
	public static Stream<CloudEvent> to(Stream<Event> events) {
		return events.map(CloudEvents::to);
	}
	
	public static CloudEvent to(Event event) {
//		return new CloudEventBuilder()
//					.withAggregateType(event.getStream())
//					.withAggregateId(event.getStreamId())
//					.withPayload(event.getData())
//					.withTimestamp(event.getTimestamp())
//					.withSubject(event.getPayload().getClass().getName())
//					.withVersion(event.getRevision())
//					.build();
		return null;
	}
	
}
