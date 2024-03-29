package eu.eventstorm.sql.csv;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * https://www.w3.org/International/questions/qa-byte-order-mark
 */
public enum ByteOrderMark {
    /**
     * UTF 8
     */
    UTF_8(new byte[]{(byte) 239, (byte) 187, (byte) 191}),
    /**
     * UTF 16 Big Endian
     */
    UTF_16_BE(new byte[]{(byte) 254, (byte) 255}),
    /**
     * UTF 16 Little Endian
     */
    UTF_16_LE(new byte[]{(byte) 255, (byte) 254}),
    /**
     * UTF 32 Big Endian
     */
    UTF_32_BE(new byte[]{(byte) 0, (byte) 0, (byte) 254, (byte) 255}),
    /**
     * UTF 32 Little Endian
     */
    UTF_32_LE(new byte[]{(byte) 255, (byte) 254, (byte) 0, (byte) 0}),
    /**
     * UTF 7
     */
    UTF_7_1(new byte[]{(byte) 43, (byte) 47, (byte) 118, (byte) 56}),
    /**
     * UTF 7
     */
    UTF_7_2(new byte[]{(byte) 43, (byte) 47, (byte) 118, (byte) 57}),
    /**
     * UTF 7
     */
    UTF_7_3(new byte[]{(byte) 43, (byte) 47, (byte) 118, (byte) 43}),
    /**
     * UTF 7
     */
    UTF_7_4(new byte[]{(byte) 43, (byte) 47, (byte) 118, (byte) 47}),
    /**
     * UTF 7
     */
    UTF_7_5(new byte[]{(byte) 43, (byte) 47, (byte) 118, (byte) 56, (byte) 45}),
    /**
     * UTF 1
     */
    UTF_1(new byte[]{(byte) 247, (byte) 100, (byte) 76}),
    /**
     * UTF EBCDIC
     */
    UTF_EBCDIC(new byte[]{(byte) 221, (byte) 115, (byte) 102, (byte) 115}),
    /**
     * SCSU
     */
    SCSU(new byte[]{(byte) 14, (byte) 254, (byte) 255}),
    /**
     * BOCU-1
     */
    BOCU_1(new byte[]{(byte) 251, (byte) 238, (byte) 40}),
    /**
     * GB-18030
     */
    GB_18030(new byte[]{(byte) 132, (byte) 49, (byte) 149, (byte) 51});

    private final byte[] value;

    ByteOrderMark(byte[] value) {
        this.value = value;
    }

    public int length() {
		return this.value.length;
	}

    public static ByteOrderMark valueOf(byte[] tag) {
        if (tag != null) {
            for (ByteOrderMark t : ByteOrderMark.values()) {
                if (Arrays.equals(tag, t.value)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static ByteOrderMark read(ByteBuffer buffer) {
        // due to java 11 : https://stackoverflow.com/questions/61267495/exception-in-thread-main-java-lang-nosuchmethoderror-java-nio-bytebuffer-flip
        // noinspection RedundantCast
        if (((java.nio.Buffer)buffer).position() != 0) {  // explicitly casting
            return null;
        }

        byte[] potentialBom;
        // noinspection RedundantCast
        if (((java.nio.Buffer)buffer).remaining() < 5) {
            // noinspection RedundantCast
            potentialBom = new byte[((java.nio.Buffer)buffer).remaining()]; // explicitly casting
        } else {
            potentialBom = new byte[5];
        }

        buffer.get(potentialBom); // explicitly casting
        ((java.nio.Buffer)buffer).position(0); // explicitly casting
        return findBom(potentialBom);
    }

    private static ByteOrderMark findBom(byte[] potentialBom) {
        ByteOrderMark toReturn = null;
        for (ByteOrderMark bom : ByteOrderMark.values()) {
            if (arrayContainsBom(potentialBom, bom)) {
                if (toReturn != null) {
                    if (bom.value.length > toReturn.value.length) {
                        toReturn = bom;
                    }
                } else {
                    toReturn = bom;
                }
            }
        }
        return toReturn;
    }

    private static boolean arrayContainsBom(byte[] potentialBom, ByteOrderMark bom) {
        int maxLength = potentialBom.length < bom.value.length ? potentialBom.length : bom.value.length;
        for (int i = 0; i < maxLength; i++) {
            if (potentialBom[i] != bom.value[i]) {
                return false;
            }
        }
        return true;
    }

}