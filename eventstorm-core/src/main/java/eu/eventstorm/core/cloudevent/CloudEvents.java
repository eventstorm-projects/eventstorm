package eu.eventstorm.core.cloudevent;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CloudEvents {

	private CloudEvents() {
	}

	public static ImmutableList<CloudEvent> to(ImmutableList<Event<? extends EventPayload>> events) {
		return events.stream().map(event -> new CloudEventBuilder()
					.aggreateType(event.getAggregateType())
					.aggregateId(event.getAggregateId())
					.payload(event.getPayload())
					.timestamp(event.getTimestamp())
					.subject(event.getPayload().getClass().getName())
					.version(event.getRevision())
					.build())
				.collect(toImmutableList());
	}
	
}
