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
class ComposeStreamIdTest {

	@Test
	void testEquals() {
		
		StreamId id1 = new ComposeStreamId(new StringStreamId("123"), new StringStreamId("456"));
		StreamId id2 = new StringStreamId("124");
		StreamId id3 = new StringStreamId("123");
		StreamId id4 = new ComposeStreamId(new StringStreamId("123"), new StringStreamId("789"));
		StreamId id5 = new ComposeStreamId(new StringStreamId("111"), new StringStreamId("456"));
		
		assertEquals(id1, id1);
		assertEquals(id1, new ComposeStreamId(new StringStreamId("123"), new StringStreamId("456")));
		assertNotEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(id1, id5);
		assertNotEquals(id1, null);
		assertNotEquals("hello", id1);
		
		assertEquals("123__456", id1.toStringValue());
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<StreamId> set = new HashSet<>();
		set.add(new ComposeStreamId(new StringStreamId("123"), new StringStreamId("456")));
		
		assertTrue(set.contains(new ComposeStreamId(new StringStreamId("123"), new StringStreamId("456"))));
		assertFalse(set.contains(new LongStreamId(124l)));
		assertFalse(set.contains(new IntegerStreamId(125)));
		
		System.out.println(new StringStreamId("123").toString());
		System.out.println(new ComposeStreamId(new StringStreamId("123"), new StringStreamId("456")).toString());
		
		JSONAssert.assertEquals("{id1:{id:\"123\"},id2:{id:\"456\"}}", new ComposeStreamId(new StringStreamId("123"), new StringStreamId("456")).toString(), false);
		
	}
}
