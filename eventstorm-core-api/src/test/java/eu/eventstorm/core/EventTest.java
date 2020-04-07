package eu.eventstorm.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import eu.eventstorm.core.id.StreamIds;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class EventTest {

	@Test
	void test01() throws JSONException {
		
		OffsetDateTime now = OffsetDateTime.now();
		
		EventPayload payload = new EventPayload() {
		};
		
		Event<EventPayload> event = new EventBuilder<>()
				.withStream("user")
				.withStreamId(StreamIds.from(1))
				.withRevision(1)
				.withTimestamp(now)
				.withPayload(payload)
				.build();
		
		assertEquals(now, event.getTimestamp());
		assertEquals("user", event.getStream());
		assertEquals(StreamIds.from(1), event.getStreamId());
		assertEquals(1, event.getRevision());
		assertEquals(payload, event.getPayload());
		
		JSONAssert.assertEquals("{stream:user, revision:1}", event.toString(), false);

	}
}
