package eu.eventstorm.core.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import eu.eventstorm.core.AggregateId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class ComposeAggreateIdTest {

	@Test
	void testEquals() {
		
		AggregateId id1 = new ComposeAggregateId(new StringAggregateId("123"), new StringAggregateId("456"));
		AggregateId id2 = new StringAggregateId("124");
		AggregateId id3 = new StringAggregateId("123");
		
		assertEquals(id1, id1);
		assertEquals(id1, new ComposeAggregateId(new StringAggregateId("123"), new StringAggregateId("456")));
		assertNotEquals(id1, id3);
		assertNotEquals(id1, id2);
		assertNotEquals(id1, null);
		assertNotEquals(id1, "hello");
		
		assertEquals("123__456", id1.toStringValue());
	}
	
	@Test
	void testHashCode() throws JSONException {
		
		Set<AggregateId> set = new HashSet<>();
		set.add(new ComposeAggregateId(new StringAggregateId("123"), new StringAggregateId("456")));
		
		assertTrue(set.contains(new ComposeAggregateId(new StringAggregateId("123"), new StringAggregateId("456"))));
		assertFalse(set.contains(new LongAggregateId(124l)));
		assertFalse(set.contains(new IntegerAggregateId(125)));
		
	}
}
