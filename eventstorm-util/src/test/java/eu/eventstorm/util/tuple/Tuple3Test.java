package eu.eventstorm.util.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class Tuple3Test {

	@Test
	void testInvalidParameters() {
		
		NullPointerException npe = assertThrows(NullPointerException.class, () -> Tuples.of(null, "right", "3"));
		assertEquals("t1 is null", npe.getMessage());
		
		npe = assertThrows(NullPointerException.class, () -> Tuples.of("left", null, ""));
		assertEquals("t2 is null", npe.getMessage());
		
		npe = assertThrows(NullPointerException.class, () -> Tuples.of("left", "right", null));
		assertEquals("t3 is null", npe.getMessage());
	}
	
	@Test
	void testNormal() {
		
		Tuple3<String, String, String> tuple3 = Tuples.of("left", "right", "3");
		Tuple3<String, String, String> tuple3_1 = Tuples.of("left", "right", "3");
		Tuple3<String, String, String> tuple3_2 = Tuples.of("left2", "right", "3");
		Tuple3<String, String, String> tuple3_3 = Tuples.of("left", "right2", "3");
		Tuple3<String, String, String> tuple3_4 = Tuples.of("left2", "right2", "3");
		Tuple3<String, String, String> tuple3_5 = Tuples.of("left", "right", "4");
		
		assertEquals(tuple3, tuple3);
		assertEquals(tuple3, tuple3_1);
		
		assertNotEquals(tuple3, tuple3_2);
		assertNotEquals(tuple3, tuple3_3);
		assertNotEquals(tuple3, tuple3_4);
		assertNotEquals(tuple3, tuple3_5);
		
		assertNotEquals(tuple3, "Bad");
		assertNotEquals(tuple3, null);
		
		assertEquals(tuple3.hashCode(), tuple3_1.hashCode());
		assertNotEquals(tuple3.hashCode(), tuple3_2.hashCode());
		assertNotEquals(tuple3.hashCode(), tuple3_3.hashCode());
		assertNotEquals(tuple3.hashCode(), tuple3_4.hashCode());
		assertNotEquals(tuple3.hashCode(), tuple3_5.hashCode());
		
		assertEquals(tuple3.toString(), tuple3_1.toString());
	}
	
}
