package eu.eventstorm.util.unsafe;

import eu.eventstorm.util.Jvm;
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
        if (Jvm.isJava8()) {
            String mutable = new String();// an empty string to hack
            UNSAFE.putObject(mutable, VALUE_OFFSET, chars);
            return mutable;
        } else {
            return new String(chars);
        }
    }

    public final static String valueOf(byte[] chars) {
        if (Jvm.isJava9OrPlus()) {
            String mutable = new String();// an empty string to hack
            UNSAFE.putObject(mutable, VALUE_OFFSET, chars);
            return mutable;
        } else {
            return new String(chars);
        }
    }

    public final static char[] getChars(String s) {
        if (Jvm.isJava8()) {
            return (char[]) UNSAFE.getObject(s, VALUE_OFFSET);
        } else {
            return s.toCharArray();
        }

    }

    public final static byte[] getBytes(String s) {
        return (byte[]) UNSAFE.getObject(s, VALUE_OFFSET);
    }

}
