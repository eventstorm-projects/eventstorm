package eu.eventstorm.annotation.els;

public enum NumberType {

    /**
     * A signed 64-bit integer with a minimum value of -263 and a maximum value of 263-1.
     */
    LONG("long"),

    /**
     * A signed 32-bit integer with a minimum value of -231 and a maximum value of 231-1.
     */
    INTEGER("integer"),

    /**
     * A signed 16-bit integer with a minimum value of -32768 and a maximum value of 32767.
     */
    SHORT("short"),

    /**
     * A signed 8-bit integer with a minimum value of -128 and a maximum value of 127.
     */
    BYTE("byte"),

    /**
     * A single-precision 32-bit IEEE 754 floating point number, restricted to finite values.
     */
    FLOAT("float"),

    /**
     * A double-precision 64-bit IEEE 754 floating point number, restricted to finite values.
     */
    DOUBLE("double");

    private final String value;

    NumberType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
