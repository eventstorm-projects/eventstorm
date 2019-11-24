package eu.eventstorm.util.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class Tuple2Test {

	@Test
	void testInvalidParameters() {
		
		NullPointerException npe = assertThrows(NullPointerException.class, () -> Tuples.of(null, "right"));
		assertEquals("t1 is null", npe.getMessage());
		
		npe = assertThrows(NullPointerException.class, () -> Tuples.of("left", null));
		assertEquals("t2 is null", npe.getMessage());
	}
	
	@Test
	void testNormal() {
		
		Tuple2<String, String> tuple2 = Tuples.of("left", "right");
		Tuple2<String, String> tuple2_1 = Tuples.of("left", "right");
		Tuple2<String, String> tuple2_2 = Tuples.of("left2", "right");
		Tuple2<String, String> tuple2_3 = Tuples.of("left", "right2");
		Tuple2<String, String> tuple2_4 = Tuples.of("left2", "right2");
		
		assertEquals(tuple2, tuple2);
		assertEquals(tuple2, tuple2_1);
		
		assertNotEquals(tuple2, tuple2_2);
		assertNotEquals(tuple2, tuple2_3);
		assertNotEquals(tuple2, tuple2_4);
		
		assertNotEquals(tuple2, "Bad");
		assertNotEquals(tuple2, null);
		
		assertEquals(tuple2.hashCode(), tuple2_1.hashCode());
		assertNotEquals(tuple2.hashCode(), tuple2_2.hashCode());
		assertNotEquals(tuple2.hashCode(), tuple2_3.hashCode());
		assertNotEquals(tuple2.hashCode(), tuple2_4.hashCode());
		
		assertEquals(tuple2.toString(), tuple2_1.toString());
	}
	
}
