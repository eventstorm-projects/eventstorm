package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class ToStringBuilderTest {

    @Test
    void testAppendCharArray() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("test", (char[]) null);
        assertEquals("{\"test\":null}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("test", "jacques".toCharArray());
        assertEquals("{\"test\":\"jacques\"}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("test", (char[]) null);
        assertEquals("{}", builder.toString());
    }

    @Test
    void testAppendString() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("test", (String) null);
        assertEquals("{\"test\":null}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("test", "mili");
        assertEquals("{\"test\":\"mili\"}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("test", (String) null);
        assertEquals("{}", builder.toString());
    }

    @Test
    void testAppendObject() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("object", (Object) null);
        assertEquals("{\"object\":null}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("object", new Toto());
        assertEquals("{\"object\":\"toto2\"}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("object", (Object) null);
        assertEquals("{}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("object", new Toto());
        assertEquals("{\"object\":\"toto2\"}", builder.toString());

    }

    @Test
    void testAppendLocalDate() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("date", (LocalDate) null);
        assertEquals("{\"date\":null}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("date", LocalDate.of(2011, 3, 9));
        assertEquals("{\"date\":[2011,3,9]}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("date", LocalDate.of(2011, 11, 12));
        assertEquals("{\"date\":[2011,11,12]}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("date", (LocalDate) null);
        assertEquals("{}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("date", LocalDate.of(2011, 11, 12));
        assertEquals("{\"date\":[2011,11,12]}", builder.toString());
    }

    @Test
    void testAppendLocalTime() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("time", (LocalTime) null);
        assertEquals("{\"time\":null}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("time", LocalTime.of(18, 36, 7));
        assertEquals("{\"time\":[18,36,7]}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("time", LocalTime.of(4, 5, 12));
        assertEquals("{\"time\":[4,5,12]}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("time", (LocalTime) null);
        assertEquals("{}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("time", LocalTime.of(4, 5, 12));
        assertEquals("{\"time\":[4,5,12]}", builder.toString());

    }

    @Test
    void testAppendLocalDateTime() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("dateTime", (LocalDateTime) null);
        assertEquals("{\"dateTime\":null}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("dateTime", LocalDateTime.of(2011, 03, 9, 18, 36, 7));
        assertEquals("{\"dateTime\":[2011,3,9,18,36,7]}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("dateTime", (LocalDateTime) null);
        assertEquals("{}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("dateTime", LocalDateTime.of(2011, 03, 9, 18, 36, 7));
        assertEquals("{\"dateTime\":[2011,3,9,18,36,7]}", builder.toString());
    }

    @Test
    void test2Append() {
        ToStringBuilder builder = new ToStringBuilder(true);
        builder.append("key1", "hello1");
        builder.append("key2", "hello2");
        assertEquals("{\"key1\":\"hello1\",\"key2\":\"hello2\"}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("key1", (String) null);
        builder.append("key2", "hello2");
        assertEquals("{\"key1\":null,\"key2\":\"hello2\"}", builder.toString());
    }

    @Test
    void testAppendManyProperties() {
        ToStringBuilder builder = new ToStringBuilder(true);
        for (int i = 0; i < 256; i++) {
            builder.append("key1", "hello1");
            builder.append("test", "hello".toCharArray());
            builder.append("date", LocalDate.of(2011, 3, 9));
            builder.append("time", LocalTime.of(18, 36, 7));
            builder.append("dateTime", LocalDateTime.of(2011, 03, 9, 18, 36, 7));
            builder.append("object", new Toto());
        }
        builder.toString();
    }

    @Test
    void testWithObject() {

    	Toto toto = new Toto();
        ToStringBuilder builder = new ToStringBuilder(toto);
        assertEquals("{\"class\":\"Toto\",\"identityHashCode\":\""+ toto.hashCode() + "\"}", builder.toString());

        builder.append("object", new Toto());
        assertEquals("{\"class\":\"Toto\",\"identityHashCode\":\""+ toto.hashCode() + "\",\"object\":\"toto2\"}", builder.toString());

    }

	@Test
	void testArray() {
		ToStringBuilder builder = new ToStringBuilder(true);
		builder.append("array", new Long[] {1L, 2L});
		assertEquals("{\"array\":[1,2]}", builder.toString());

		builder = new ToStringBuilder(true);
		builder.append("array", new String[] {"1", "2"});
		assertEquals("{\"array\":[\"1\",\"2\"]}", builder.toString());

    }

    private static final class Toto {

        @Override
        public String toString() {
            return "toto2";
        }
    }

}