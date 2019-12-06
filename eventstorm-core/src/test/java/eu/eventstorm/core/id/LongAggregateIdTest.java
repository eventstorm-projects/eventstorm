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
class LongAggregateIdTest {

	@Test
	void testEquals() {
		
		AggregateId id1 = new LongAggregateId(123l);
		AggregateId id2 = new LongAggregateId(124l);
		AggregateId id3 = new LongAggregateId(123l);
		AggregateId id4 = new IntegerAggregateId(123);
		
		assertEquals(id1, id1);
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id4);
		assertNotEquals(id1, null);
		
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<AggregateId> set = new HashSet<>();
		set.add(new LongAggregateId(123l));
		
		assertTrue(set.contains(new LongAggregateId(123l)));
		assertFalse(set.contains(new LongAggregateId(124l)));
		assertFalse(set.contains(new IntegerAggregateId(125)));
		
		JSONAssert.assertEquals("{id:123}", new LongAggregateId(123l).toString(), false);

		
	}
}
