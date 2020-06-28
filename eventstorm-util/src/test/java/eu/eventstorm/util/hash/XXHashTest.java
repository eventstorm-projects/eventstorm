package eu.eventstorm.util.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import eu.eventstorm.util.unsafe.UnsafeHelper;
import eu.eventstorm.util.unsafe.UnsafeString;

class XXHashTest {

	@Test
	void test() {
		String value = "Hello World !";
		assertEquals(-7739591154577084421l, Hashing.xx(123456, value));
		assertEquals(-7739591154577084421l, Hashing.xx(123456, new HashReader4String(UnsafeString.getChars(value))));
		
		value = "Hello World !Hello World !Hello World !Hello World !Hello World !Hello World !Hello World !";
		assertEquals(2875214966217480447l, Hashing.xx(123456, value));
		assertEquals(2875214966217480447l, Hashing.xx(123456, new HashReader4String(UnsafeString.getChars(value))));
	}

	@SuppressWarnings("restriction")
	private static class HashReader4String extends HashReader {

		private final char[] val;
		private long adr;

		public HashReader4String(char[] val) {
			this.val = val;
			this.adr = 16;
		}

		@Override
		public long length() {
			return val.length * 2;
		}

		@Override
		public long getLong() {
			long value = UnsafeHelper.getUnsafe().getLong(val, adr);
			adr += 8;
			return value;
		}

		@Override
		public int getInt() {
			int  value = UnsafeHelper.getUnsafe().getInt(val, adr);
			adr +=4;
			return value;
		}

		@Override
		public long getByte() {
			return UnsafeHelper.getUnsafe().getByte(val, adr++);
		}
	}
	
}