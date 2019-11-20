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

class IntegerAggreateIdTest {

	@Test
	void testEquals() {
		
		AggregateId id1 = new IntegerAggreateId(123);
		AggregateId id2 = new IntegerAggreateId(124);
		AggregateId id3 = new IntegerAggreateId(123);
		AggregateId id4 = new LongAggreateId(123l);
		
		assertEquals(id1, id1);
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(id1, null);
		
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<AggregateId> set = new HashSet<>();
		set.add(new IntegerAggreateId(123));
		
		assertTrue(set.contains(new IntegerAggreateId(123)));
		assertFalse(set.contains(new IntegerAggreateId(124)));
		assertFalse(set.contains(new LongAggreateId(125l)));
		

		JSONAssert.assertEquals("{id:123}", new IntegerAggreateId(123).toString(), false);
	}
}
