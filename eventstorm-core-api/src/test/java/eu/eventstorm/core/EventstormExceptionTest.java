package eu.eventstorm.core;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

class EventstormExceptionTest {

	@Test
	void testNormal() {
		
		SimpleEventstormException ex = new SimpleEventstormException(SimpleEventstormException.Type.MONO, ImmutableMap.of("key","value"));
		
		assertEquals(SimpleEventstormException.Type.MONO, ex.getType());
		assertEquals("value", ex.getValues().get("key"));
		
		ex = new SimpleEventstormException(SimpleEventstormException.Type.MONO, null);
		
		assertEquals(SimpleEventstormException.Type.MONO, ex.getType());
		assertNull(ex.getValues());
		
		ex = new SimpleEventstormException(SimpleEventstormException.Type.MONO, ImmutableMap.of("key","value"), new IllegalStateException());
		
		assertEquals(SimpleEventstormException.Type.MONO, ex.getType());
		assertEquals("value", ex.getValues().get("key"));
		
		
	}
	
	@SuppressWarnings("serial")
	static class SimpleEventstormException extends EventstormException {

		enum Type implements EventstormExceptionType {
			MONO
		}
		
		SimpleEventstormException(EventstormExceptionType type, ImmutableMap<String, Object> values) {
			super(type, values);
		}

		SimpleEventstormException(EventstormExceptionType type, ImmutableMap<String, Object> values, Throwable cause) {
			super(type, values, cause);
		}
	
		
	}
}
