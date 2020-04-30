package eu.eventstorm.cloudevents;

import org.junit.jupiter.api.Test;

class CloudeventTest {

	@Test
	void test() {
		
//		EventStoreClient eventStore = new InMemoryEventStoreClient(
//				new InMemoryStreamManagerBuilder()
//					.withDefinition("toto")
//						.withPayload(EventPayload.class, null, null)
//					.and()
//					.build(),
//				new InMemoryEventStore()
//				);
//		
//		eventStore.appendToStream("toto", from(12), new EventPayload() {});
//		eventStore.appendToStream("toto", from(12), new EventPayload() {});
//		eventStore.appendToStream("toto", from(12), new EventPayload() {});
//		eventStore.appendToStream("toto", from(12), new EventPayload() {});
//		
//		List<CloudEvent> events = CloudEvents.to(eventStore.readStream("toto", from(12))).collect(toImmutableList());
//		
//		for (CloudEvent event : events) {
//			assertEquals("12", event.id());
//			assertEquals("1.0", event.specVersion());
//		}
//		
//		events = CloudEvents.to(eventStore.readStream("toto", from(12)).collect(toImmutableList())).collect(toImmutableList());
//		
//		for (CloudEvent event : events) {
//			assertEquals("12", event.id());
//			assertEquals("1.0", event.specVersion());
//		}
	}
	
	@Test
	void testBuilder() throws Exception {
//		
//		CloudEvent event = new CloudEventBuilder()
//				.withAggregateType("ag-type")
//				.withAggregateId(from(12))
//				.withSpecVersion("1.0")
//				.withSubject("subject__1234567890")
//				.withTimestamp(OffsetDateTime.now())
//				.withVersion(4)
//				.withPayload(new EventPayload() {
//				})
//				.build();
//		
//		assertEquals("ag-type", event.type());
//		assertEquals("12", event.id());
//		assertEquals("1.0", event.specVersion());
//		assertEquals("subject__1234567890", event.subject());
//		
//		JSONAssert.assertEquals("{aggregateType:\"ag-type\"}", event.toString(), false);
//		JSONAssert.assertEquals("{aggregateId:{id:12}}", event.toString(), false);
	}
}
