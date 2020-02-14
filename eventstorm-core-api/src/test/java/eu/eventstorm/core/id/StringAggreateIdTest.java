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
class StringAggreateIdTest {

	@Test
	void testEquals() {
		
		AggregateId id1 = new StringAggregateId("123");
		AggregateId id2 = new StringAggregateId("124");
		AggregateId id3 = new StringAggregateId("123");
		AggregateId id4 = new IntegerAggregateId(123);
		
		assertEquals(id1, id1);
		assertEquals(id1, AggregateIds.from("123"));
		assertEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertEquals(id1, id4);
		assertNotEquals(null, id1);
		assertNotEquals("hello", id1);
		
		assertEquals("123", id1.toStringValue());
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<AggregateId> set = new HashSet<>();
		set.add(new StringAggregateId("123"));
		
		assertTrue(set.contains(new StringAggregateId("123")));
		assertFalse(set.contains(new LongAggregateId(124l)));
		assertFalse(set.contains(new IntegerAggregateId(125)));
		
		JSONAssert.assertEquals("{id:\"123\"}", new StringAggregateId("123").toString(), false);

		
	}
}
