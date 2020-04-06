package eu.eventstorm.eventstore.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.eventstore.StreamDefinitionException;

class InMemoryStreamDefinitionTest {

	@Test
	void testGetStreamEvantPayloadDefinition() {
		
		InMemoryStreamDefinition def = new InMemoryStreamDefinition("test", ImmutableList.of(
				new InMemoryStreamEvantPayloadDefinition<>("user", "userCreated", SimpleInterface.class,  null, null)
				));
		
		assertNotNull(def.getStreamEvantPayloadDefinition(new SimpleInterface() {}));
		assertNotNull(def.getStreamEvantPayloadDefinition(new SimpleInterface2() {}));
		assertNotNull(def.getStreamEvantPayloadDefinition(new SimpleClass()));
		assertNotNull(def.getStreamEvantPayloadDefinition(new SimpleClass2()));
		
		//valid cache
		assertNotNull(def.getStreamEvantPayloadDefinition(new SimpleClass()));
		assertNotNull(def.getStreamEvantPayloadDefinition(new SimpleClass2()));
		
		StreamDefinitionException ex = assertThrows(StreamDefinitionException.class, () -> def.getStreamEvantPayloadDefinition(new EventPayload() {}));		
		assertEquals(StreamDefinitionException.Type.INVALID_STREAM_PAYLOAD_CLASS, ex.getType());
	}
	
	
	
	static interface SimpleInterface extends EventPayload {
	}
	
	static interface SimpleInterface2 extends SimpleInterface {
	}

	static class SimpleClass implements SimpleInterface {
	}
	
	static class SimpleClass2 implements SimpleInterface2 {
	}
	
}
