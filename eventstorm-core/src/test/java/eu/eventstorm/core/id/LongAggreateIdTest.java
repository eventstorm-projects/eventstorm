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

import eu.eventstorm.core.AggregateId;

class LongAggreateIdTest {

	@Test
	void testEquals() {
		
		AggregateId id1 = new LongAggreateId(123l);
		AggregateId id2 = new LongAggreateId(124l);
		AggregateId id3 = new LongAggreateId(123l);
		AggregateId id4 = new IntegerAggreateId(123);
		
		assertEquals(id1, id1);
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(id1, null);
		
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<AggregateId> set = new HashSet<>();
		set.add(new LongAggreateId(123l));
		
		assertTrue(set.contains(new LongAggreateId(123l)));
		assertFalse(set.contains(new LongAggreateId(124l)));
		assertFalse(set.contains(new IntegerAggreateId(125)));
		
		JSONAssert.assertEquals("{id:123}", new LongAggreateId(123l).toString(), false);

		
	}
}
