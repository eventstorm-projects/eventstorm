package eu.eventstorm.core.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class StringAggreateIdTest {

	@Test
	void testEquals() {
		
		StreamId id1 = new StringStreamId("123");
		StreamId id2 = new StringStreamId("124");
		StreamId id3 = new StringStreamId("123");
		StreamId id4 = new IntegerStreamId(123);
		
		assertEquals(id1, id1);
		assertEquals(id1, StreamIds.from("123"));
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertEquals(id1, id4);
		assertNotEquals(null, id1);
		assertNotEquals("hello", id1);
		
		assertEquals("123", id1.toStringValue());
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<StreamId> set = new HashSet<>();
		set.add(new StringStreamId("123"));
		
		assertTrue(set.contains(new StringStreamId("123")));
		assertFalse(set.contains(new LongStreamId(124l)));
		assertFalse(set.contains(new IntegerStreamId(125)));
		
		JSONAssert.assertEquals("{id:\"123\"}", new StringStreamId("123").toString(), false);

		
	}
}
