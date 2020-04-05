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
class LongStreamIdTest {

	@Test
	void testEquals() {
		
		StreamId id1 = new LongStreamId(123l);
		StreamId id2 = new LongStreamId(124l);
		StreamId id3 = StreamIds.from(123l);
		StreamId id4 = new IntegerStreamId(123);
		
		assertEquals(id1, id1);
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(null, id1);
		
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<StreamId> set = new HashSet<>();
		set.add(new LongStreamId(123l));
		
		assertTrue(set.contains(new LongStreamId(123l)));
		assertFalse(set.contains(new LongStreamId(124l)));
		assertFalse(set.contains(new IntegerStreamId(125)));
		
		JSONAssert.assertEquals("{id:123}", new LongStreamId(123l).toString(), false);

		
	}
}
