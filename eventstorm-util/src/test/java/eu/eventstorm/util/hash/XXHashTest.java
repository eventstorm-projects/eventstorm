package eu.eventstorm.util.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.eventstorm.util.Jvm;
import org.junit.jupiter.api.Test;

import eu.eventstorm.util.unsafe.UnsafeHelper;
import eu.eventstorm.util.unsafe.UnsafeString;

class XXHashTest {

	@Test
	void test() {
		String value_1 = "Hello World !";
		String value_2 = "Hello World !Hello World !Hello World !Hello World !Hello World !Hello World !Hello World !";

		if (Jvm.isJava8()) {
			assertEquals(-7739591154577084421l, Hashing.xx(123456, value_1));
			//assertEquals(-7739591154577084421l, Hashing.xx(123456, new HashReader4String(UnsafeString.getChars(value))));

			assertEquals(2875214966217480447l, Hashing.xx(123456, value_2));
			//assertEquals(2875214966217480447l, Hashing.xx(123456, new HashReader4String(UnsafeString.getChars(value))));
		} else {
			assertEquals(8404568533873697733l, Hashing.xx(123456, value_1));
			assertEquals(8404568533873697733l, Hashing.xx(123456, new HashReader4Byte(UnsafeString.getBytes(value_1))));

			assertEquals(-2982664739075951601l, Hashing.xx(123456, value_2));
			assertEquals(-2982664739075951601l, Hashing.xx(123456, new HashReader4Byte(UnsafeString.getBytes(value_2))));
		}

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


	private static class HashReader4Byte extends HashReader {

		private final byte[] val;
		private long adr;

		public HashReader4Byte(byte[] val) {
			this.val = val;
			this.adr = 16;
		}

		@Override
		public long length() {
			return val.length;
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