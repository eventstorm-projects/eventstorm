package eu.eventstorm.core.id;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 * 
 * @see https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format-00
 */
public final class UniversalUniqueIdentifierV6 extends UniversalUniqueIdentifier {

	private static final Clock CLOCK = Clock.systemUTC();
	
    private UniversalUniqueIdentifierV6(UUID uuid) {
    	super(uuid);
	}
    
    public LocalDateTime getLocalDateTime() {
    	long t = ((getUuid().getMostSignificantBits() >> 4) & 0xFFFFFFFFFFFFF000L) | (0x0FFF & getUuid().getMostSignificantBits());
    	return LocalDateTime.ofInstant(Instant.ofEpochMilli(t), ZoneId.systemDefault());
    }
    
    public short getRegion() {
    	return (short) (getUuid().getLeastSignificantBits() >> 48);
    }
    
    public short getNode() {
    	return (short) (getUuid().getLeastSignificantBits() >> 32);
    }
    
    public int getRandom() {
    	return (int) getUuid().getLeastSignificantBits();
    }
    
    public static UniversalUniqueIdentifierV6 from(Clock clock, short region, short  node, int random) {
    	long ts = clock.millis();
    	return new UniversalUniqueIdentifierV6(new UUID((ts << 4) & 0xFFFFFFFFFFFF0000L | (ts & 0x0FFF) | 0x6000L, 
    			(((long)region) << 48) + (((long)node) << 32) + random)); 
    }

    public static UniversalUniqueIdentifierV6 from(short region, short  node, int random) {
    	return from(CLOCK, region, node, random);
    }

    
}
