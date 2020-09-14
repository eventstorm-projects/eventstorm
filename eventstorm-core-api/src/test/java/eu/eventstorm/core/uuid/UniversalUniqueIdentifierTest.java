package eu.eventstorm.core.uuid;

import static eu.eventstorm.core.uuid.UniversalUniqueIdentifierV6.from;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@ext.europarl.europa.eu">Jacques Militello</a>
 */
class UniversalUniqueIdentifierTest {

	@Test
	void testVersion6() {
		Clock clock = Clock.systemUTC(); 
		UniversalUniqueIdentifierV6 uuid = from(clock, (short)15, (short)4568, 123456789);
		assertEquals(15, uuid.getRegion());
		assertEquals(4568, uuid.getNode());
		assertEquals(123456789, uuid.getRandom());
		
		LocalDateTime actual = uuid.getLocalDateTime();
		LocalDateTime expected = LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
		
		assertEquals(expected.getYear(), actual.getYear());
		assertEquals(expected.getMonth(), actual.getMonth());
		assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
		
		assertEquals(expected.getHour(), actual.getHour());
		assertEquals(expected.getMinute(), actual.getMinute());
		
		
		assertEquals(6, uuid.getUuid().version());
	}
	
	@Test
	void testFactory() {
		
		UniversalUniqueIdentifierV6 uuid = UniversalUniqueIdentifiers.generate(new UniversalUniqueIdentifierDefinition() {
			public short getRegion() {
				return 0x0100;
			}
			public short getNode() {
				return 0x1111;
			}
		});
		
		assertEquals(256, uuid.getRegion());
		assertEquals(4369, uuid.getNode());
		assertEquals(1, uuid.getRandom());
		
	}
	
}