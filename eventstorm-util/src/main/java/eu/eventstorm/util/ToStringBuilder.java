package eu.eventstorm.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static eu.eventstorm.util.unsafe.UnsafeString.getChars;
import static eu.eventstorm.util.unsafe.UnsafeString.valueOf;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ToStringBuilder {

    /**
     * Default size for the buffer.
     */
    private static final int DEFAULT = 1024;

    /**
     * char buffer for a null object.
     */
    private static final char[] NULL = new char[]{'n', 'u', 'l', 'l'};

    /**
     * char buffer for a null object.
     */
    private static final char[] CLASS = new char[]{'c', 'l', 'a', 's', 's'};

    /**
     * char buffer for a null object.
     */
    private static final char[] IDENTITY_HASH_CODE = new char[]{'i', 'd', 'e', 'n', 't', 'i', 't', 'y', 'H', 'a', 's', 'h', 'C', 'o', 'd', 'e'};

    /**
     * Code ascii for char '0' -> 48.
     */
    private static final byte ASCII_CODE_0 = 48;

    /**
     * The buffer for this builder.
     */
    private char[] value;

    /**
     * The index for this buffer (for char[] value).
     */
    private int idx;

    /**
     * boolean to test if we append null value or not.
     */
    private final boolean appendNull;

    /**
     * Instantiates a new to string builder.
     */
    public ToStringBuilder(boolean appendNull) {
        this.value = new char[DEFAULT];
        this.value[0] = '{';
        this.idx = 1;
        this.appendNull = appendNull;
    }

    public ToStringBuilder(Object object) {
        this(object, true);
    }

    public ToStringBuilder(Object object, boolean appendNull) {
        this(appendNull);
        // add "class":"...."
        insertKey(CLASS);
        insertValue(getChars(object.getClass().getSimpleName()));
        // add "identityHashCode":"....."
        insertKey(IDENTITY_HASH_CODE);
        insertValue(getChars(String.valueOf(System.identityHashCode(object))));
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, String value) {
        if (!this.appendNull && value == null) {
            return this;
        }
        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            insertValue(getChars(value));
        }
        return this;
    }

    public ToStringBuilder append(String key, String value, int max) {
        if (!this.appendNull && value == null) {
            return this;
        }
        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            if (value.length() > max) {
                insertValue(getChars(value.substring(0, max)));
            } else {
                insertValue(getChars(value));
            }
        }
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, char[] value) {
        if (!this.appendNull && value == null) {
            return this;
        }

        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            insertValue(value);
        }
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, Object value) {
        if (!this.appendNull && value == null) {
            return this;
        }
        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            if (value.getClass().isArray()) {
                insertRaw(getChars(toString((Object[]) value)));
            } else {
                this.insertValue(value);
            }
        }
        return this;
    }

    /**
     * Append a java.time.LocalDate
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, LocalDate value) {
        if (!this.appendNull && value == null) {
            return this;
        }
        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            insertValue(value);
        }
        return this;
    }

    /**
     * Append a java.time.LocalTime.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, LocalTime value) {
        if (!this.appendNull && value == null) {
            return this;
        }
        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            insertValue(value);
        }
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, LocalDateTime value) {
        if (!this.appendNull && value == null) {
            return this;
        }
        insertKey(getChars(key));
        if (value == null) {
            insertNullValue();
        } else {
            insertValue(value);
        }
        return this;
    }

    /**
     * Append.
     *
     * @param key    the key
     * @param values the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, List<?> values) {
        if (!this.appendNull && values == null) {
            return this;
        }
        insertKey(getChars(key));
        if (values == null) {
            insertNullValue();
        } else if (values.isEmpty()) {
            addChar('[');
            addChar(']');
            addChar(',');
        } else {
            addChar('[');
            values.forEach(this::insertValue);
            this.value[idx - 1] = ']';
            addChar(',');
        }
        return this;
    }

    /**
     * Append.
     *
     * @param key the key
     * @param map the map
     * @return the to string builder
     */
    public ToStringBuilder append(String key, Map<?, ?> map) {
        if (!this.appendNull && map == null) {
            return this;
        }

        insertKey(getChars(key));
        if (map == null) {
            insertNullValue();
        } else {
            addChar('[');
            for (Object keyMap : map.keySet()) {
                addChar('{');
                insertValue(getChars(keyMap.toString()));
                this.value[idx - 1] = ':';
                insertValue(getChars(String.valueOf(map.get(keyMap))));
                this.value[idx - 1] = '}';
                addChar(',');
            }
            this.value[idx - 1] = ']';
            addChar(',');
        }
        return this;
    }

    private void addChar(char c) {
        if (idx + 1 > this.value.length) {
            expandCapacity(idx + 1);
        }
        this.value[idx++] = c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        int i = this.idx;
        char[] v;

        if (this.value[i - 1] == ',') {
            v = new char[i];
            System.arraycopy(this.value, 0, v, 0, i - 1);
            v[i - 1] = '}';
        } else {
            v = new char[i + 1];
            System.arraycopy(this.value, 0, v, 0, i);
            v[i] = '}';
        }
        return valueOf(v);
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = this.value.length << 1;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }
        this.value = Arrays.copyOf(this.value, newCapacity);
    }

    private void insertValue(Object value) {
        if (Number.class.isAssignableFrom(value.getClass())) {
            insertRaw(getChars(value.toString()));
        } else {
            char[] content = getChars(value.toString());
            if (content.length > 1 && content[0] == '{' && content[content.length - 1] == '}') {
                insertRaw(content);
            } else {
                insertValue(content);
            }
        }
    }

    private void insertKey(char[] value) {
        int index = this.idx;
        int len = value.length;
        int max = index + len + 3;
        if (max > this.value.length) {
            expandCapacity(max);
        }
        char[] val = this.value;
        val[index] = '"';
        System.arraycopy(value, 0, this.value, index + 1, len);
        val[max - 2] = '"';
        val[max - 1] = ':';
        this.idx = max;
    }

    private void insertValue(char[] value) {
        int len = value.length;
        int max = this.idx + len + 3;
        if (max > this.value.length) {
            expandCapacity(max);
        }
        char[] val = this.value;
        val[this.idx] = '"';
        System.arraycopy(value, 0, this.value, this.idx + 1, len);
        val[max - 2] = '"';
        val[max - 1] = ',';
        this.idx = max;
    }

    private void insertRaw(char[] value) {
        int len = value.length;
        int max = this.idx + len + 1;
        if (max > this.value.length) {
            expandCapacity(max);
        }
        char[] val = this.value;
        System.arraycopy(value, 0, this.value, this.idx, len);
        val[max - 1] = ',';
        this.idx = max;
    }

    private void insertValue(LocalDateTime dateTime) {
        int ptr = this.idx;
        int max = ptr + 24;
        if (max > this.value.length) {
            expandCapacity(max);
        }
        this.value[ptr++] = '[';
        ptr = appendValue(this.value, ptr, dateTime.toLocalDate());
        this.value[ptr++] = ',';
        ptr = appendValue(this.value, ptr, dateTime.toLocalTime());
        this.value[ptr++] = ']';
        this.value[ptr++] = ',';
        this.idx = ptr;
    }

    private void insertValue(LocalDate date) {
        int ptr = this.idx;
        if (ptr + 12 > this.value.length) {
            expandCapacity(ptr + 12);
        }
        this.value[ptr++] = '[';
        ptr = appendValue(this.value, ptr, date);
        this.value[ptr++] = ']';
        this.value[ptr++] = ',';
        this.idx = ptr;
    }

    private void insertValue(LocalTime time) {
        int ptr = this.idx;
        if (ptr + 10 > this.value.length) {
            expandCapacity(ptr + 10);
        }
        this.value[ptr++] = '[';
        ptr = appendValue(this.value, ptr, time);
        this.value[ptr++] = ']';
        this.value[ptr++] = ',';
        this.idx = ptr;
    }

    private void insertNullValue() {
        int max = this.idx + 5;
        if (max > this.value.length) {
            expandCapacity(max);
        }
        System.arraycopy(NULL, 0, this.value, this.idx, 4);
        this.value[max - 1] = ',';
        this.idx = max;
    }

    private static int appendValue(char[] val, int ptr, LocalDate date) {
        int year = date.getYear();
        val[ptr + 3] = (char) (ASCII_CODE_0 + year % 10);
        year = Maths.unsignedDiv10(year);
        val[ptr + 2] = (char) (ASCII_CODE_0 + year % 10);
        year = Maths.unsignedDiv10(year);
        val[ptr + 1] = (char) (ASCII_CODE_0 + year % 10);
        val[ptr] = (char) (ASCII_CODE_0 + (Maths.unsignedDiv1000(date.getYear())));
        ptr += 4;
        val[ptr++] = ',';

        // reuse year as temp;
        year = Maths.unsignedDiv10(date.getMonthValue());
        if (year != 0) {
            val[ptr++] = (char) (ASCII_CODE_0 + year);
        }
        val[ptr++] = (char) (ASCII_CODE_0 + date.getMonthValue() % 10);
        val[ptr++] = ',';

        year = Maths.unsignedDiv10(date.getDayOfMonth());
        if (year != 0) {
            val[ptr++] = (char) (ASCII_CODE_0 + year);
        }
        val[ptr++] = (char) (ASCII_CODE_0 + date.getDayOfMonth() % 10);
        return ptr;
    }

    private static int appendValue(char[] val, int ptr, LocalTime time) {
        int temp;
        temp = Maths.unsignedDiv10(time.getHour());
        if (temp != 0) {
            val[ptr++] = (char) (ASCII_CODE_0 + temp);
        }
        val[ptr++] = (char) (ASCII_CODE_0 + time.getHour() % 10);
        val[ptr++] = ',';

        temp = Maths.unsignedDiv10(time.getMinute());
        if (temp != 0) {
            val[ptr++] = (char) (ASCII_CODE_0 + temp);
        }
        val[ptr++] = (char) (ASCII_CODE_0 + time.getMinute() % 10);
        val[ptr++] = ',';

        temp = Maths.unsignedDiv10(time.getSecond());
        if (temp != 0) {
            val[ptr++] = (char) (ASCII_CODE_0 + temp);
        }
        val[ptr++] = (char) (ASCII_CODE_0 + time.getSecond() % 10);
        return ptr;
    }

    private static String toString(Object[] a) {

        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {

            if (a[i] instanceof Number) {
                b.append(a[i]);
            } else {
                b.append('"');
                b.append(a[i]);
                b.append('"');
            }
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(",");
        }
    }

}