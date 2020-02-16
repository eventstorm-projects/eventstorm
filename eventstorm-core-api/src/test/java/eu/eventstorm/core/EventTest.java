package eu.eventstorm.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import eu.eventstorm.core.id.AggregateIds;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class EventTest {

	@Test
	void test01() {
		
		OffsetDateTime now = OffsetDateTime.now();
		
		EventPayload payload = new EventPayload() {
		};
		
		Event<EventPayload> event = new EventBuilder<>()
				.withAggreateType("user")
				.withAggregateId(AggregateIds.from(1))
				.withRevision(1)
				.withTimestamp(now)
				.withPayload(payload)
				.build();
		
		assertEquals(now, event.getTimestamp());
		assertEquals("user", event.getAggregateType());
		assertEquals(AggregateIds.from(1), event.getAggregateId());
		assertEquals(1, event.getRevision());
		assertEquals(payload, event.getPayload());
				
	}
}
