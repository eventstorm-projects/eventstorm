package eu.eventstorm.util.unsafe;

import sun.misc.Unsafe;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings("all")
public final class UnsafeString {

    private static final long VALUE_OFFSET;

    private static final Unsafe UNSAFE;

    static {
        UNSAFE = UnsafeHelper.getUnsafe();
        VALUE_OFFSET = UnsafeHelper.getFieldOffset(String.class, "value");
    }

    private UnsafeString() {
    }

    public final static String valueOf(char[] chars) {
        return new String(chars);
    }

    public final static String valueOf(byte[] chars) {
        String mutable = new String();// an empty string to hack
        UNSAFE.putObject(mutable, VALUE_OFFSET, chars);
        return mutable;
    }

    public final static char[] getChars(String s) {
        return s.toCharArray();
    }

    public final static byte[] getBytes(String s) {
        return (byte[]) UNSAFE.getObject(s, VALUE_OFFSET);
    }

}
