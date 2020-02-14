package eu.eventstorm.eventstore.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eu.eventstorm.annotation.CqrsEventPayload;
import eu.eventstorm.core.DomainModel;
import eu.eventstorm.core.EventPayload;

class EventPayloadRegistryBuilderTest {

	@Test
	void testNotInterface() {
		
		EventPayloadRegistryBuilder builder = new EventPayloadRegistryBuilder();
		EventPayloadRegistryBuilderException ex = assertThrows(EventPayloadRegistryBuilderException.class, () -> builder.add(BadEventPayload.class, null, null));
		
		assertEquals(EventPayloadRegistryBuilderException.Type.NOT_INTERFACE, ex.getType());
	}
	
	@Test
	void testMissingAnnotation() {
		
		EventPayloadRegistryBuilder builder = new EventPayloadRegistryBuilder();
		EventPayloadRegistryBuilderException ex = assertThrows(EventPayloadRegistryBuilderException.class, () -> builder.add(MissingAnnotationEventPayload.class, null, null));
		
		assertEquals(EventPayloadRegistryBuilderException.Type.MISSING_ANNOTATION_CQRS_EVENTPAYLOAD, ex.getType());
	}
	
	@Test
	void testDuplicate() {
		
		EventPayloadRegistryBuilder builder = new EventPayloadRegistryBuilder();
		builder.add(GoodEventPayload.class, null, null);
		EventPayloadRegistryBuilderException ex = assertThrows(EventPayloadRegistryBuilderException.class, () -> builder.add(GoodEventPayload.class, null, null));
		
		assertEquals(EventPayloadRegistryBuilderException.Type.DUPLICATE_TYPE, ex.getType());
	}
	
	
	private static class BadEventPayload implements EventPayload {
		
	}
	
	private static interface MissingAnnotationEventPayload extends EventPayload {
		
	}
	
	@CqrsEventPayload(domain = DomainModel.class)
	private static interface GoodEventPayload extends EventPayload {
		
	}
	
}
