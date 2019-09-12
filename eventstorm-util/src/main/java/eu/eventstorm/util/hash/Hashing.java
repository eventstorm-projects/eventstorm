package eu.eventstorm.util.hash;

import eu.eventstorm.util.unsafe.UnsafeHelper;
import eu.eventstorm.util.unsafe.UnsafeString;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Hashing {

	private static final XXHash XX_HASH_INSTANCE = new XXHash();

	private Hashing() {
	}
	
	public static long xx(long seed, String value) {
		char[] op = UnsafeString.getChars(value);
		return XX_HASH_INSTANCE.xxHash64(seed, UnsafeHelper.arrayCharBaseOffset(), op.length * 2l, op);
	}
	
	public static long xx(long seed, HashReader reader) {
		return XX_HASH_INSTANCE.xxHash64(seed, reader);
	}
}
