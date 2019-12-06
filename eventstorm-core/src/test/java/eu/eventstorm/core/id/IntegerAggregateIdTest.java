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

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class IntegerAggregateIdTest {

	@Test
	void testEquals() {
		
		AggregateId id1 = new IntegerAggregateId(123);
		AggregateId id2 = new IntegerAggregateId(124);
		AggregateId id3 = new IntegerAggregateId(123);
		AggregateId id4 = new LongAggregateId(123l);
		
		assertEquals(id1, id1);
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(id1, null);
		
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<AggregateId> set = new HashSet<>();
		set.add(new IntegerAggregateId(123));
		
		assertTrue(set.contains(new IntegerAggregateId(123)));
		assertFalse(set.contains(new IntegerAggregateId(124)));
		assertFalse(set.contains(new LongAggregateId(125l)));
		

		JSONAssert.assertEquals("{id:123}", new IntegerAggregateId(123).toString(), false);
	}
}
