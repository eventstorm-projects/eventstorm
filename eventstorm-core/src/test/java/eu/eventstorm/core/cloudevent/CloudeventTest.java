package eu.eventstorm.core.cloudevent;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static eu.eventstorm.core.id.AggregateIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.eventstore.InMemoryEventStore;

class CloudeventTest {

	@Test
	void test() {
		InMemoryEventStore eventStore = new InMemoryEventStore();
		
		eventStore.appendToStream("toto", from(12), new EventPayload() {});
		eventStore.appendToStream("toto", from(12), new EventPayload() {});
		eventStore.appendToStream("toto", from(12), new EventPayload() {});
		eventStore.appendToStream("toto", from(12), new EventPayload() {});
		
		List<CloudEvent> events = CloudEvents.to(eventStore.readStream("toto", from(12))).collect(toImmutableList());
		
		for (CloudEvent event : events) {
			assertEquals("12", event.id());
			assertEquals("1.0", event.specVersion());
		}
		
		events = CloudEvents.to(eventStore.readStream("toto", from(12)).collect(toImmutableList())).collect(toImmutableList());
		
		for (CloudEvent event : events) {
			assertEquals("12", event.id());
			assertEquals("1.0", event.specVersion());
		}
	}
	
	@Test
	void testBuilder() throws Exception {
		
		CloudEvent event = new CloudEventBuilder()
				.withAggregateType("ag-type")
				.withAggregateId(from(12))
				.withSpecVersion("1.0")
				.withSubject("subject__1234567890")
				.withTimestamp(OffsetDateTime.now())
				.withVersion(4)
				.withPayload(new EventPayload() {
				})
				.build();
		
		assertEquals("ag-type", event.type());
		assertEquals("12", event.id());
		assertEquals("1.0", event.specVersion());
		assertEquals("subject__1234567890", event.subject());
		
		JSONAssert.assertEquals("{aggregateType:\"ag-type\"}", event.toString(), false);
		JSONAssert.assertEquals("{aggregateId:{id:12}}", event.toString(), false);
	}
}
