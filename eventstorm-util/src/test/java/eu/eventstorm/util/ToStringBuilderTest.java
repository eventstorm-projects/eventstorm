package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
    
        builder = new ToStringBuilder(false);
        builder.append("test", "jacques".toCharArray());
        assertEquals("{\"test\":\"jacques\"}", builder.toString());
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
        
        builder = new ToStringBuilder(false);
        builder.append("test", "mili");
        assertEquals("{\"test\":\"mili\"}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("test", "mili", 2);
        assertEquals("{\"test\":\"mi\"}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("test", "mili", 5);
        assertEquals("{\"test\":\"mili\"}", builder.toString());
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

        builder = new ToStringBuilder(true);
        builder.append("number", 456789);
        assertEquals("{\"number\":456789}", builder.toString());
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
        builder.append("dateTime", LocalDateTime.of(2011, 3, 9, 18, 36, 7));
        assertEquals("{\"dateTime\":[2011,3,9,18,36,7]}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("dateTime", (LocalDateTime) null);
        assertEquals("{}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("dateTime", LocalDateTime.of(2011, 3, 9, 18, 36, 7));
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
            builder.append("dateTime", LocalDateTime.of(2011, 3, 9, 18, 36, 7));
            builder.append("object", new Toto());
        }
        assertEquals("{\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\",\"key1\":\"hello1\",\"test\":\"hello\",\"date\":[2011,3,9],\"time\":[18,36,7],\"dateTime\":[2011,3,9,18,36,7],\"object\":\"toto2\"}", builder.toString());

    }

    @Test
    void testWithObject() {

    	Toto toto = new Toto();
        ToStringBuilder builder = new ToStringBuilder(toto);
        assertEquals("{\"class\":\"Toto\",\"identityHashCode\":\""+ toto.hashCode() + "\"}", builder.toString());

        builder.append("object", new Toto());
        assertEquals("{\"class\":\"Toto\",\"identityHashCode\":\""+ toto.hashCode() + "\",\"object\":\"toto2\"}", builder.toString());

        TotoObject object = new TotoObject();
        builder = new ToStringBuilder(object, false);
        builder.append("object", object);
        assertEquals("{\"class\":\"TotoObject\",\"identityHashCode\":\""+ object.hashCode() + "\",\"object\":{\"name\":\"toto\"}}", builder.toString());
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
	
	@Test
	void testList() {
		ToStringBuilder builder = new ToStringBuilder(true);
		builder.append("list", ImmutableList.of(1L, 2L));
		assertEquals("{\"list\":[1,2]}", builder.toString());

		builder = new ToStringBuilder(true);
		builder.append("list", ImmutableList.of("1", "2"));
		assertEquals("{\"list\":[\"1\",\"2\"]}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("list", ImmutableList.of());
        assertEquals("{\"list\":[]}", builder.toString());

        builder = new ToStringBuilder(true);
        builder.append("list", (List) null);
        assertEquals("{\"list\":null}", builder.toString());

        builder = new ToStringBuilder(false);
        builder.append("list", (List) null);
        assertEquals("{}", builder.toString());
    }
	
	@Test
	void testMap() {
		ToStringBuilder builder = new ToStringBuilder(true);
		builder.append("map", ImmutableMap.of("key1", "key2"));
		builder.append("map2", ImmutableMap.of(1, 2, 3, 4));
		builder.append("map3", ImmutableMap.of(new Toto(), "2", "3", new Toto()));
		assertEquals("{\"map\":[{\"key1\":\"key2\"}],\"map2\":[{\"1\":\"2\"},{\"3\":\"4\"}],\"map3\":[{\"toto2\":\"2\"},{\"3\":\"toto2\"}]}", builder.toString());
  
		builder = new ToStringBuilder(true);
		builder.append("map", (Map<?,?>)null);
		assertEquals("{\"map\":null}", builder.toString());
		
		builder = new ToStringBuilder(false);
		builder.append("map", (Map<?,?>)null);
		assertEquals("{}", builder.toString());

		builder = new ToStringBuilder(false);
		builder.append("map", ImmutableMap.of("key1", "key2"));
		builder.append("map2", ImmutableMap.of(1, 2, 3, 4));
		builder.append("map3", ImmutableMap.of(new Toto(), "2", "3", new Toto()));
		assertEquals("{\"map\":[{\"key1\":\"key2\"}],\"map2\":[{\"1\":\"2\"},{\"3\":\"4\"}],\"map3\":[{\"toto2\":\"2\"},{\"3\":\"toto2\"}]}", builder.toString());

	
	}

    private static final class Toto {

        @Override
        public String toString() {
            return "toto2";
        }
    }

    private static final class TotoObject {

        @Override
        public String toString() {
            return "{\"name\":\"toto\"}";
        }
    }

}