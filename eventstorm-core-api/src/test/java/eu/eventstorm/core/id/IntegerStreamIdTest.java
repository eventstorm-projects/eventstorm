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
class IntegerStreamIdTest {

	@Test
	void testEquals() {
		
		StreamId id1 = new IntegerStreamId(123);
		StreamId id2 = new IntegerStreamId(124);
		StreamId id3 = new IntegerStreamId(123);
		StreamId id4 = new LongStreamId(123l);
		
		assertEquals(id1, id1);
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(null, id1);
		assertNotEquals(id1, null);
		
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<StreamId> set = new HashSet<>();
		set.add(new IntegerStreamId(123));
		
		assertTrue(set.contains(new IntegerStreamId(123)));
		assertFalse(set.contains(new IntegerStreamId(124)));
		assertFalse(set.contains(new LongStreamId(125l)));
		

		JSONAssert.assertEquals("{id:123}", new IntegerStreamId(123).toString(), false);
	}
}
