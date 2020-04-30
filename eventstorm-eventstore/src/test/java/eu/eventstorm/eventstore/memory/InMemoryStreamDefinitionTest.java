package eu.eventstorm.eventstore.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.eventstore.StreamDefinitionException;

class InMemoryStreamDefinitionTest {

	@Test
	void testGetStreamEvantPayloadDefinition() {
//		
//		InMemoryStreamDefinition def = new InMemoryStreamDefinition("test", ImmutableList.of(
//				new InMemoryStreamEventDefinition<>("user", "userCreated", SimpleInterface.class,  null, null)
//				));
//		
//		assertNotNull(def.getStreamEventDefinition(new SimpleInterface() {}));
//		assertNotNull(def.getStreamEventDefinition(new SimpleInterface2() {}));
//		assertNotNull(def.getStreamEventDefinition(new SimpleClass()));
//		assertNotNull(def.getStreamEventDefinition(new SimpleClass2()));
//		
//		//valid cache
//		assertNotNull(def.getStreamEventDefinition(new SimpleClass()));
//		assertNotNull(def.getStreamEventDefinition(new SimpleClass2()));
//		
//		StreamDefinitionException ex = assertThrows(StreamDefinitionException.class, () -> def.getStreamEventDefinition(new EventPayload() {}));		
//		assertEquals(StreamDefinitionException.Type.INVALID_STREAM_PAYLOAD_CLASS, ex.getType());
	}
	
	
	
}
